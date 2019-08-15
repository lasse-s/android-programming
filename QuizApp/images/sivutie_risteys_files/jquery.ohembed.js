(function ($) {

    $.oembed = {};

    $.oembed.defaultConfig = {
        width: 640,
        height: 360,
        onEmbed: function () {
        }
    };

    $.oembed.apis = {
        youtube: "https://gdata.youtube.com/feeds/api/videos/#tag#?v=2&alt=json-in-script&callback=?",
        vimeo: "https://vimeo.com/api/oembed.json?url=#tag#&callback=?"
    };

    $.oembed.schemes = {
        youtube: [
            "https?:\/\/(www\.)?youtube\\.com/watch.+v=[\\w-]+&?",
            "https?:\/\/(www\.)?youtu\\.be/[\\w-]+",
            "https?:\/\/(www\.)?youtube.com/embed"
        ],
        vimeo: [
            "https?:\/\/(www\.)?vimeo\.com\/groups\/.*\/videos\/.*",
            "https?:\/\/(www\.)?vimeo\.com\/channels\/.*\/.*",
            "https?:\/\/(www\.)?vimeo\.com\/.*"
        ]
    };

    $.oembed.extractors = {
        youtube: /.*(?:v\=|be\/|embed\/)([\w\-]+)&?.*/
    };

    $.oembed.templates = {
        youtube: "<iframe src=\"https://www.youtube.com/embed/#tag#\" width=\"#width#\" height=\"#height#\" frameborder=\"0\" allowfullscreen></iframe>"
    };

    $.oembed.getHTML = function (provider, tag, config) {
        var template = this.templates[provider];
        if (template) {
            var html = template
                .replace("#tag#", tag)
                .replace("#width#", config.width)
                .replace("#height#", config.height)
            config.onEmbed.call(this, provider, html);
        } else {
            var url = this.apis[provider].replace('#tag#', tag);
            $.getJSON(url, null, function (data) {
                config.onEmbed.call(this, provider, data.html);
            });
        }
    };

    $.oembed.match = function (url, config) {
        for (var provider in this.schemes) {
            var schemes = this.schemes[provider];
            var extractor = this.extractors[provider];
            for (var i = 0; i < schemes.length; i++) {
                var regExp = new RegExp(schemes[i], "i");
                var match = url.match(regExp);
                if (!(match !== null)) {
                    continue;
                }
                if (extractor) {
                    var tag = url.match(extractor)[1];
                } else {
                    var tag = match[0];
                }
                tag = encodeURIComponent(tag);
                return this.getHTML(provider, tag, config)
            }
        }
    };

    $.fn.oembed = function (url, config) {
        return $.oembed.match(url, $.extend({}, $.oembed.defaultConfig, config));
    };

})(jQuery);
