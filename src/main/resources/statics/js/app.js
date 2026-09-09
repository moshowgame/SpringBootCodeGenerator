/**
 * CodeGenApp — 大狼狗代码生成器前端应用（jQuery3 + Bootstrap 5.3 + CodeMirror 5）
 *
 * 独立命名空间实现，与旧版 main.js 完全隔离，可共存互不影响。
 * 页面约定：
 *   - [data-action]      按钮行为（generate / copy / download-zip / toggle-theme）
 *   - [data-cg-opt]      生成选项控件（name 即 options key）
 *   - [data-template]    模板按钮（name 即模板名）
 *   - #cg-input/#cg-output  CodeMirror 挂载点
 */
(function (window, document) {
  'use strict';

  var $ = window.jQuery;
  var axios = window.axios;
  var CodeMirror = window.CodeMirror;

  var API = {
    generate: (window.basePath || '') + '/code/generate',
    generateZip: (window.basePath || '') + '/code/generate-zip',
    templates: (window.basePath || '') + '/template/all'
  };

  var COOKIE_KEYS = ['authorName', 'packageName', 'returnUtilSuccess', 'returnUtilFailure',
    'ignorePrefix', 'tinyintTransType', 'timeTransType'];
  var COOKIE_AGE = 365 * 24 * 3600;
  var HISTORY_LIMIT = 9;
  var HISTORY_PREFIX = 'cg:history:';
  var THEME_KEY = 'cg:theme';
  var FOLD_KEY = 'cg:templates-fold';
  var TEMPLATE_KEY = 'cg:template';

  var SAMPLE_SQL = 'CREATE TABLE \'sys_user_info\' (\n' +
    '  \'user_id\' int(11) NOT NULL AUTO_INCREMENT COMMENT \'用户编号\',\n' +
    '  \'user_name\' varchar(255) NOT NULL COMMENT \'用户名\',\n' +
    '  \'status\' tinyint(1) NOT NULL COMMENT \'状态\',\n' +
    '  \'create_time\' datetime NOT NULL COMMENT \'创建时间\',\n' +
    '  PRIMARY KEY (\'user_id\')\n' +
    ') ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT=\'用户信息\'';

  var state = {
    options: {
      dataType: 'sql',
      authorName: '',
      packageName: '',
      returnUtilSuccess: '',
      returnUtilFailure: '',
      ignorePrefix: 'sys_',
      tinyintTransType: 'int',
      nameCaseType: 'CamelCase',
      timeTransType: 'Date',
      isPackageType: true,
      isSwagger: false,
      isAutoImport: false,
      isWithPackage: false,
      isComment: true,
      isLombok: true,
      isHeritage: true
    },
    templates: [],
    fileNameMap: {},
    history: [],
    currentTemplate: 'plusentity',
    outputJson: {},
    oemOutputStr: '',
    foldOpen: false
  };

  var els = {};
  var editors = { input: null, output: null };

  /* ============================== utils ============================== */

  function notify(type, msg) {
    if (window.toastr) {
      toastr[type === 'warning' ? 'warning' : (type === 'error' ? 'error' : 'success')](msg);
    } else {
      window.alert(msg);
    }
  }

  function escapeHtml(str) {
    return String(str == null ? '' : str).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  /* ============================== cookies ============================== */

  function writeCookie(key, val) {
    try {
      document.cookie = key + '=' + encodeURIComponent(val == null ? '' : val) +
        ';path=' + (window.basePath || '/') + ';max-age=' + COOKIE_AGE;
    } catch (e) { /* ignore */ }
  }

  function readCookie(key) {
    try {
      var pairs = document.cookie.split(';');
      for (var i = 0; i < pairs.length; i++) {
        var kv = pairs[i].split('=');
        if (kv[0].replace(/^\s+/, '') === key) {
          return decodeURIComponent(kv.slice(1).join('='));
        }
      }
    } catch (e) { /* ignore */ }
    return '';
  }

  function saveCookies() {
    COOKIE_KEYS.forEach(function (key) {
      writeCookie(key, state.options[key]);
    });
  }

  function loadCookies() {
    COOKIE_KEYS.forEach(function (key) {
      var val = readCookie(key);
      if (val !== '') {
        state.options[key] = val;
      }
    });
  }

  /* ============================== theme ============================== */

  function resolveInitialTheme() {
    try {
      var saved = localStorage.getItem(THEME_KEY);
      if (saved === 'dark' || saved === 'light') {
        return saved;
      }
    } catch (e) { /* ignore */ }
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    return 'light';
  }

  function editorTheme() {
    return document.documentElement.getAttribute('data-bs-theme') === 'dark'
      ? 'material-darker' : 'idea';
  }

  function applyTheme(mode, persist) {
    document.documentElement.setAttribute('data-bs-theme', mode);
    if (editors.input) {
      editors.input.setOption('theme', editorTheme());
    }
    if (editors.output) {
      editors.output.setOption('theme', editorTheme());
    }
    if (els.themeBtn.length) {
      els.themeBtn.html(mode === 'dark' ? '<i class="bi bi-sun"></i>' : '<i class="bi bi-moon-stars"></i>');
    }
    if (persist) {
      try { localStorage.setItem(THEME_KEY, mode); } catch (e) { /* ignore */ }
    }
  }

  function toggleTheme() {
    applyTheme(document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark', true);
  }

  /* ============================== editors ============================== */

  function initEditors() {
    editors.input = CodeMirror.fromTextArea(document.getElementById('cg-input'), {
      mode: 'text/x-sql',
      theme: editorTheme(),
      lineNumbers: true,
      smartIndent: true,
      autoCloseBrackets: true,
      styleActiveLine: true
    });
    editors.output = CodeMirror.fromTextArea(document.getElementById('cg-output'), {
      mode: 'text/x-java',
      theme: editorTheme(),
      lineNumbers: true,
      smartIndent: true,
      readOnly: false,
      autoCloseBrackets: true
    });
    editors.input.setValue(SAMPLE_SQL);
  }

  function modeForTemplate(name) {
    var fileName = (state.fileNameMap[name] || name).toLowerCase();
    if (/\.(xml|html|vue)$/.test(fileName)) { return 'application/xml'; }
    if (/\.sql$/.test(fileName)) { return 'text/x-sql'; }
    if (/\.json$/.test(fileName)) { return 'application/json'; }
    if (/\.(jjs|js)$/.test(fileName)) { return 'text/javascript'; }
    return 'text/x-java';
  }

  function setOutput(text) {
    editors.output.setOption('mode', modeForTemplate(state.currentTemplate));
    editors.output.setValue(text == null ? '' : text);
  }

  /* ============================== options ============================== */

  function applyOptionsToDom() {
    $('[data-cg-opt]').each(function () {
      var key = this.getAttribute('data-cg-opt');
      var val = state.options[key];
      if (this.type === 'checkbox') {
        this.checked = val === true || val === 'true';
      } else {
        this.value = val == null ? '' : val;
      }
    });
  }

  function collectOptions() {
    $('[data-cg-opt]').each(function () {
      state.options[this.getAttribute('data-cg-opt')] =
        this.type === 'checkbox' ? this.checked : this.value;
    });
  }

  /* ============================== templates ============================== */

  function loadTemplates() {
    axios.post(API.templates, {}).then(function (res) {
      state.templates = (res.data && res.data.data) || [];
      state.templates.forEach(function (group) {
        (group.templates || []).forEach(function (t) {
          state.fileNameMap[t.name] = t.fileName || '';
        });
      });
      renderTemplates();
    }).catch(function () {
      els.templates.html('<div class="text-danger small">模板加载失败，请刷新页面重试</div>');
    });
  }

  function renderGroup(group) {
    var html = '<div class="cg-template-row">' +
      '<span class="cg-template-label">' + escapeHtml(group.group) + '</span>' +
      '<div class="d-flex flex-wrap gap-1">';
    (group.templates || []).forEach(function (t) {
      var active = t.name === state.currentTemplate;
      html += '<button type="button" class="btn btn-sm cg-template-btn ' +
        (active ? 'btn-primary' : 'btn-outline-secondary') + '" data-template="' +
        escapeHtml(t.name) + '"' + (active ? ' aria-pressed="true"' : '') + ' title="' +
        escapeHtml(t.description || t.name) + '">' + escapeHtml(t.name) + '</button>';
    });
    html += '</div></div>';
    return html;
  }

  function renderTemplates() {
    if (!els.templates.length) { return; }
    var mainHtml = '';
    var legacyHtml = '';
    var legacyCount = 0;
    var currentInLegacy = false;
    state.templates.forEach(function (group) {
      var rowHtml = renderGroup(group);
      if (group.tier === 'legacy') {
        legacyHtml += rowHtml;
        legacyCount++;
        (group.templates || []).forEach(function (t) {
          if (t.name === state.currentTemplate) { currentInLegacy = true; }
        });
      } else {
        mainHtml += rowHtml;
      }
    });
    var html = mainHtml;
    if (legacyHtml) {
      var open = state.foldOpen || currentInLegacy;
      html += '<details class="cg-fold" id="cgFold"' + (open ? ' open' : '') + '>' +
        '<summary><i class="bi bi-archive me-1"></i>更多模板 · 低频/遗产组（' + legacyCount + ' 组）</summary>' +
        '<div class="cg-fold-body">' + legacyHtml + '</div></details>';
    }
    els.templates.html(html || '<div class="text-body-secondary small">未找到模板</div>');
    var fold = document.getElementById('cgFold');
    if (fold) {
      fold.addEventListener('toggle', function () {
        state.foldOpen = fold.open;
        try { localStorage.setItem(FOLD_KEY, fold.open ? 'open' : 'closed'); } catch (e) { /* ignore */ }
      });
    }
  }

  function selectTemplate(name) {
    state.currentTemplate = name;
    try { localStorage.setItem(TEMPLATE_KEY, name); } catch (e) { /* ignore */ }
    renderTemplates();
    var content = state.outputJson[name];
    setOutput(typeof content === 'string' ? content.trim() : '');
    if (els.currentTemplate.length) {
      if (state.outputJson && state.outputJson.tableName) {
        els.currentTemplate.text(name).show();
      } else {
        els.currentTemplate.hide();
      }
    }
  }

  /* ============================== history ============================== */

  function saveHistory(tableName) {
    if (!tableName) { return; }
    try {
      sessionStorage.setItem(HISTORY_PREFIX + tableName, JSON.stringify(state.outputJson));
    } catch (e) { /* ignore */ }
    var idx = state.history.indexOf(tableName);
    if (idx >= 0) { state.history.splice(idx, 1); }
    state.history.unshift(tableName);
    if (state.history.length > HISTORY_LIMIT) {
      var removed = state.history.splice(HISTORY_LIMIT);
      removed.forEach(function (t) {
        try { sessionStorage.removeItem(HISTORY_PREFIX + t); } catch (e) { /* ignore */ }
      });
    }
    renderHistory();
  }

  function renderHistory() {
    if (!els.historyChips.length) { return; }
    if (!state.history.length) {
      els.historyBar.hide();
      return;
    }
    els.historyBar.show();
    els.historyChips.html(state.history.map(function (name) {
      return '<button type="button" class="btn btn-outline-secondary btn-sm cg-chip" data-history="' +
        escapeHtml(name) + '"><i class="bi bi-clock-history me-1"></i>' + escapeHtml(name) + '</button>';
    }).join(''));
  }

  function selectHistory(tableName) {
    var raw = null;
    try { raw = sessionStorage.getItem(HISTORY_PREFIX + tableName); } catch (e) { /* ignore */ }
    if (!raw) {
      notify('error', '历史记录已失效');
      return;
    }
    try {
      state.outputJson = JSON.parse(raw);
    } catch (e) {
      notify('error', '历史记录解析失败');
      return;
    }
    selectTemplate(state.currentTemplate);
    notify('success', '已切换到 ' + tableName);
  }

  /* ============================== actions ============================== */

  function setBusy(busy) {
    els.generateBtn.prop('disabled', busy);
    els.zipBtn.prop('disabled', busy);
  }

  function generate() {
    var tableSql = editors.input.getValue();
    if (!tableSql || tableSql.trim().length < 5) {
      notify('warning', '请先输入 SQL / JSON 表结构');
      return;
    }
    collectOptions();
    setBusy(true);
    axios.post(API.generate, { tableSql: tableSql, options: state.options }).then(function (res) {
      if (res.status === 500 || (res.data && res.data.code === 500)) {
        notify('error', '生成失败，请检查 SQL 语句！' + (res.data && res.data.msg ? ' ' + res.data.msg : ''));
        return;
      }
      state.outputJson = (res.data && res.data.data) || {};
      saveCookies();
      saveHistory(state.outputJson.tableName);
      selectTemplate(state.currentTemplate);
      notify('success', '生成成功');
    }).catch(function (err) {
      notify('error', '生成失败：' + (err && err.message ? err.message : '网络异常'));
    }).finally(function () {
      setBusy(false);
    });
  }

  function fallbackCopy(text) {
    var ta = document.createElement('textarea');
    ta.value = text;
    ta.style.position = 'fixed';
    ta.style.opacity = '0';
    document.body.appendChild(ta);
    ta.select();
    try {
      document.execCommand('copy');
      notify('success', '已复制');
    } catch (e) {
      notify('error', '复制失败，请手动选择复制');
    }
    document.body.removeChild(ta);
  }

  function copyOutput() {
    var text = editors.output.getValue();
    if (!text || !text.trim()) {
      notify('warning', '输出内容为空');
      return;
    }
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(function () {
        notify('success', '已复制');
      }).catch(function () {
        fallbackCopy(text);
      });
    } else {
      fallbackCopy(text);
    }
  }

  function downloadZip() {
    var tableSql = editors.input.getValue();
    if (!tableSql || tableSql.trim().length < 5) {
      notify('warning', '请先输入 SQL / JSON 表结构');
      return;
    }
    collectOptions();
    setBusy(true);
    axios.post(API.generateZip, { tableSql: tableSql, options: state.options },
      { responseType: 'blob', timeout: 60000 }).then(function (res) {
      if (res.status !== 200) {
        notify('error', '下载失败，HTTP 状态码：' + res.status);
        return;
      }
      var dispo = res.headers && (res.headers['content-disposition'] || res.headers['Content-Disposition']);
      var fileName = 'code-generator.zip';
      if (dispo) {
        var matchStar = /filename\*=UTF-8''([^;]+)/i.exec(dispo);
        var matchQuoted = /filename="?([^";]+)"?/i.exec(dispo);
        if (matchStar && matchStar[1]) {
          fileName = decodeURIComponent(matchStar[1]);
        } else if (matchQuoted && matchQuoted[1]) {
          fileName = matchQuoted[1];
        }
      }
      var blob = new Blob([res.data], { type: 'application/zip' });
      var url = window.URL.createObjectURL(blob);
      var a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      notify('success', '已下载：' + fileName);
    }).catch(function (err) {
      notify('error', '下载失败：' + (err && err.message ? err.message : '未知错误'));
    }).finally(function () {
      setBusy(false);
    });
  }

  /* ============================== bootstrap ============================== */

  var actions = {
    'generate': generate,
    'copy': copyOutput,
    'download-zip': downloadZip,
    'toggle-theme': toggleTheme
  };

  function cacheEls() {
    els.themeBtn = $('#cgThemeBtn');
    els.templates = $('#cgTemplates');
    els.historyBar = $('#cgHistoryBar');
    els.historyChips = $('#cgHistoryChips');
    els.currentTemplate = $('#cgCurrentTemplate');
    els.generateBtn = $('[data-action="generate"]');
    els.zipBtn = $('[data-action="download-zip"]');
  }

  function bindEvents() {
    $(document).on('click', '[data-action]', function (e) {
      e.preventDefault();
      var fn = actions[$(this).data('action')];
      if (fn) { fn(); }
    });
    els.templates.on('click', 'button[data-template]', function () {
      selectTemplate($(this).data('template'));
    });
    els.historyChips.on('click', 'button[data-history]', function () {
      selectHistory($(this).data('history'));
    });
  }

  function init() {
    cacheEls();
    if (window.toastr) {
      toastr.options = { positionClass: 'toast-top-center', timeOut: 2200, preventDuplicates: true };
    }
    initEditors();
    applyTheme(resolveInitialTheme(), false);
    loadCookies();
    try { state.foldOpen = localStorage.getItem(FOLD_KEY) === 'open'; } catch (e) { /* ignore */ }
    try {
      var savedTemplate = localStorage.getItem(TEMPLATE_KEY);
      if (savedTemplate) { state.currentTemplate = savedTemplate; }
    } catch (e) { /* ignore */ }
    applyOptionsToDom();
    bindEvents();
    loadTemplates();
    if (state.oemOutputStr) {
      setOutput(state.oemOutputStr);
    }
  }

  /* ============================== public api ============================== */

  window.CodeGenApp = {
    boot: function (config) {
      config = config || {};
      ['authorName', 'packageName', 'returnUtilSuccess', 'returnUtilFailure'].forEach(function (k) {
        if (config[k]) { state.options[k] = config[k]; }
      });
      state.oemOutputStr = config.outputStr || '';
      if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
      } else {
        init();
      }
    },
    generate: generate,
    copy: copyOutput,
    downloadZip: downloadZip,
    toggleTheme: toggleTheme
  };

})(window, document);
