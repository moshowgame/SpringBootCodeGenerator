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
		}
	},
	created: function () {

	},
	updated: function () {
	}
});

