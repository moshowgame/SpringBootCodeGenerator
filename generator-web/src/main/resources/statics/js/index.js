//iframe自适应
$(window).on('resize', function() {
	const $content = $('.content');
	$content.height($(this).height() - 154);
	$content.find('iframe').each(function() {
		$(this).height($content.height());
	});
}).resize();

const vm = new Vue({
	el: '#rrapp',
	data: {
		main: "main",
	},
	methods: {
		donate: function () {
			layer.open({
				type: 2,
				title: false,
				area: ['806px', '467px'],
				closeBtn: 1,
				shadeClose: false,
				content: ['https://github.com/moshowgame/SpringBootCodeGenerator/blob/master/donate.png?raw=true', 'no']
			});
		}
	},
	created: function () {

	},
	updated: function () {
	}
});

