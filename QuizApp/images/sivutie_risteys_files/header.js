jQuery(function ($) {
    // Megamenu functionality
    (function (megamenu, $, undefined) {
        var menuDelay = 100;
        var megamenuSelector = ".megamenu";
        var megamenuButtonSelector = ".megamenu-button";
        var openClass = "open";
        var closedClass = "closed";

        megamenu.init = function() {
            $(document).click(function(event) {
                if(!$(event.target).closest(megamenuSelector).length) {
                    megamenu.toggle($(megamenuSelector + " ." + openClass).parent())
                }
            });
        };

        megamenu.toggle = function (megamenuContainer) {
            if (megamenuContainer) {
                var megamenuObject = megamenuContainer.children(megamenuSelector);
                var megamenuButton = megamenuContainer.children(megamenuButtonSelector);

                if (megamenuObject.hasClass(openClass)) {
                    _close(megamenuObject, megamenuButton, null);
                    return;
                }

                // Close possible open megamenus
                var openedMegamenu = $(megamenuSelector + "." + openClass);
                if (openedMegamenu.length != 0) {
                    var openedMegamenuParent = openedMegamenu.parent();
                    _close(
                        openedMegamenu,
                        openedMegamenuParent.children(megamenuButtonSelector),
                        function () {
                            _open(megamenuObject, megamenuButton, null)
                        }
                    );
                } else {
                    _open(megamenuObject, megamenuButton, null);
                }
            } else {
                return;
            }
        };

        function _close(megamenuContainer, megamenuButton, completedCallback) {
            var closeCompleted = function () {
                megamenuContainer.removeClass(openClass);
                megamenuContainer.addClass(closedClass);
                megamenuButton.children(".fa").addClass("fa-plus-circle").removeClass("fa-minus-circle");

                if (completedCallback !== null) {
                    completedCallback();
                }
            }
            megamenuContainer.fadeOut(menuDelay, closeCompleted);
        }

        function _open(megamenuContainer, megamenuButton, completedCallback) {
            var openCompleted = function () {
                megamenuContainer.addClass(openClass);
                megamenuContainer.removeClass(closedClass);
                megamenuButton.children(".fa").addClass("fa-minus-circle").removeClass("fa-plus-circle");

                if (completedCallback !== null) {
                    completedCallback();
                }
            }
            megamenuContainer.fadeIn(menuDelay, openCompleted);
        }

    }(window.megamenu = window.megamenu || {}, jQuery));

    (function (fixedHeader, $, undefined) {
        var headerPanelSelector = ".header-group";
        var stickiedClassName = "stickied";
        var headerPanel = $(headerPanelSelector);
        var stickThreshold = 500;
        var isStickied = false;


        fixedHeader.stick = function () {
            if (isStickied == false && _isStickied() == true) {
                headerPanel.sticky({topSpacing: 0});
                headerPanel.addClass(stickiedClassName);
                isStickied = true;
            } else if (isStickied == true && _isUnstickied() == true) {
                headerPanel.unstick();
                headerPanel.removeClass(stickiedClassName);
                isStickied = false;
            }
        };

        function _getCurrentScroll() {
            return window.pageYOffset || document.documentElement.scrollTop;
        }

        function _isStickied() {
            return _getCurrentScroll() > stickThreshold;
        }

        function _isUnstickied() {
            return _getCurrentScroll() < stickThreshold;
        }
    }(window.fixedHeader = window.fixedHeader || {}, jQuery));

    (function (mobileHeader, $, undefined) {
        mobileHeader.initNavigation = function () {
            $(".child-toggle").click(function () {
                // Close all other items if top nav item is pressed
                if($(this).parent().hasClass("top-navigation-item") && !$(this).hasClass("open")) {
                    var openedMenus = $("button.child-toggle.open");
                    openedMenus.children(".fa").removeClass("fa-minus-circle").addClass("fa-plus-circle");
                    openedMenus.next().slideToggle(1).addClass("hidden"); // has to be something for toggle to work
                    openedMenus.toggleClass("open");
                }

                $(this).toggleClass('open');
                $(this).next().slideToggle(200).toggleClass('hidden');

                var circle = $(this).children(".fa");
                if ($(this).next().hasClass('hidden')) {
                    circle.removeClass("fa-minus-circle").addClass("fa-plus-circle");
                } else {
                    circle.removeClass("fa-plus-circle").addClass("fa-minus-circle");
                }

                $(this).focus();
            });
        };

        mobileHeader.init = function () {
            var $mobileBanner = $('#mobile-banner');
            var $mobileHeaderContent = $mobileBanner.find('.mobile-header-content');

            $('.mobile-search-toggle').click(function () {

                $(this).toggleClass('active');
                $('.mobile-navigation-toggle').removeClass('active');

                $('.mobile-navigation').removeClass('active');
                $('.mobile-search').toggleClass('active');

                if ($(this).hasClass('active')) {
                    $mobileBanner.addClass('fixed');
                    $mobileHeaderContent.addClass('active');
                } else {
                    $mobileBanner.removeClass('fixed');
                    $mobileHeaderContent.removeClass('active');
                }
            });

            $('.mobile-navigation-toggle').click(function () {

                $(this).toggleClass('active');
                $('.mobile-search-toggle').removeClass('active');

                $('.mobile-search').removeClass('active');
                $('.mobile-navigation').toggleClass('active');

                if ($(this).hasClass('active')) {
                    $mobileBanner.addClass('fixed');
                    $mobileHeaderContent.addClass('active');
                    $.scrollToCurrentLink();
                } else {
                    $mobileBanner.removeClass('fixed');
                    $mobileHeaderContent.removeClass('active');
                }
            });
        };

        mobileHeader.initSticky = function () {
            var $htmlAndBody = $('html, body');
            var $mobileBanner = $('#mobile-banner');
            $mobileBanner.sticky({topSpacing: 0});
            $mobileBanner.find('.mobile-navigation-toggle').click(function () {
                $htmlAndBody.toggleClass('noscroll');
            });
            $mobileBanner.find('.mobile-search-toggle').click(function () {
                $htmlAndBody.removeClass('noscroll');
            });
        };

    }(window.mobileHeader = window.mobileHeader || {}, jQuery));

    //Scrolls to currently selected link on mobile navigation when menu is opened
    $.scrollToCurrentLink = function() {
        var link_to_scroll = $('#goto_link');
        //Check if element exists
        if (link_to_scroll.length) {
            //Get element to scroll to and scroll the view to it.
            link_to_scroll[0].scrollIntoView({behavior: "smooth", block: "center", inline: "center"});
        }
    };

    /**
     * Overlays header and calculates possible hero content position and height.
     *
     *   Usage:
     *     - Mark the need for overlay with class 'hero-overlay'
     *     - Mark the overlayed div with class 'hero-content-area', this should contain the overlayed picture
     *     - Mark the content area between 'hero-content-area' bottom and header bottom with class 'hero-content'
     */
    (function (headerOverlay, $, undefined) {
        var header = null;
        var hasHeroOverlay = false;

        // Call before init, sets header overlay, calculations right after adding the class are not 100% reliable in every environment
        headerOverlay.preInit = function () {
            header = $("header#banner");
            hasHeroOverlay = $(".hero .hero-overlay").length != 0;

            if(_hasHeroOverlay()) {
                header.addClass("overlay");
            }
        };

        headerOverlay.init = function () {
            if(_hasHeroOverlay()) {
                var heroContent = $(".hero-content");
                // Set content below overlayed header
                setTimeout(function () {heroContent.css("top", header.height());}, 0);
                // Set content height in relation to hero-content-area height
                setTimeout(function () {heroContent.height($(".hero-content-area").height() - header.height());}, 0);
            }
        };

        function _hasHeroOverlay () {
            return header.length != 0 && hasHeroOverlay;
        }
    }(window.headerOverlay = window.headerOverlay || {}, jQuery));
});
