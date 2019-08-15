jQuery(function ($) {
    (function( nanogallery, $, undefined ) {
        var nanogallerySelector = ".nanogallery"

        // Maps html data-attributes to nanogallery api options and inits nanogallery.
        nanogallery.init = function() {
            var nanogallery = $(nanogallerySelector)
            if(nanogallery) {
                nanogallery.each(function() {
                    var options = _getOptions($(this));
                    if(options) {
                        _insertCaption($(nanogallerySelector), options["albumName"], options["albumUrl"]);
                        $(this).nanoGallery(options);
                    } else {
                        console.log("Invalid nanogallery options");
                    }
                });
            }
        };

        /* Get options from html data-attributes
         *
         * Usage: Add html element data-attributes, camelCase maps to - attribute name
         *      - thumbnailHeight : data-thumbnail-height="200"
         *      - userID : data-user-i-d="userid"
         *
         * Return: Options for nanogallery
         */
        function _getOptions(nanogallery) {
            var options = {
                kind: "flickr",
                thumbnailHoverEffect: [{name: "labelAppear75", duration: 300}],
                thumbnailDisplayInterval: 10,
                thumbnailDisplayTransition: true,
                viewer: "fancybox",
                fancyBoxOptions: {autoPlay:false, nextEffect:"none", prevEffect:"none", scrolling:"no"},
                i18n: {
                    paginationPrevious : langProperties.nanogalleryPrevious,
                    paginationNext : langProperties.nanogalleryNext,
                    breadcrumbHome : langProperties.nanogalleryBreadcrumbHome
                }
            };

            var data = nanogallery.data();
            for(var i in data){
                options[i] = data[i];

                if(i == "albumUrl") {
                    var photoset = _getPhotoset(data[i]);
                    if (photoset) {
                        options["photoset"] = photoset;
                    } else {
                        return;
                    }
                }
            }

            return options;
        }

        function _getPhotoset(albumUrl) {
            var photoset = "";
            if(albumUrl.indexOf("http") != -1) {
                if(albumUrl.charAt(albumUrl.length - 1) == "/") {
                    albumUrl = albumUrl.substr(0, albumUrl.length - 2);
                }
                var albumUrlArray = albumUrl.split("/");
                photoset = albumUrlArray[albumUrlArray.length - 1];
            }

            return photoset;
        }

        function _insertCaption(element, albumName, albumUrl) {
            var caption = langProperties.nanogalleryCaption;
            if(albumName) {
                caption = caption + " " + albumName;
            }
            element.prepend('<div class="nanogallery-caption"><a class="flickr-link" href="'+ albumUrl + '" target="_blank">' + caption + '<i class="fa fa-external-link-square"></i></a></div>')
        }
    }( window.nanogallery = window.nanogallery || {}, jQuery ));
});