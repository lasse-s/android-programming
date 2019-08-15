jQuery(function ($) {
// Add initialization code here
    (function( theme, $, undefined ) {
        theme.init = function () {
            // Adhoc mobile detection
            var bodyWidth = $("body").width();
            var isPhone = bodyWidth < 768;
            var isDesktop = bodyWidth > 1024;

            // Mobile header
            if(!isDesktop) {
                mobileHeader.init();
                mobileHeader.initNavigation();
                mobileHeader.initSticky();
            }

            if(isDesktop) {
                // Megamenu
                megamenu.init();
                $(".megamenu-button").click(function() {
                    megamenu.toggle($(this).parent());
                });

                // Fixed header
                $(window).scroll(function() { fixedHeader.stick() });

                headerOverlay.preInit();
            }

            // Carousel
            var slider = $('.bxslider');
            if(slider.length) {
                var bxsliderOptions = {
                    pagerCustom: ".carousel-article .container",
                    maxSlides: 4,
                    touchEnabled: false
                };

                if(isPhone) {
                    bxsliderOptions.onSliderLoad = function() {
                        $("#mobile-0").addClass("active");
                    };
                    bxsliderOptions.onSlideBefore = function($slideElement, oldIndex, newIndex) {
                        $("#mobile-" + oldIndex).removeClass("active");
                        $("#mobile-" + newIndex).addClass("active");
                    };
                } else {
                    bxsliderOptions.controls = false;
                    bxsliderOptions.infiniteLoop = false;
                }

                slider.bxSlider(bxsliderOptions);
            }


            // Search page header
            var gss = $('#p_p_id_gsssearchresults_WAR_gssportlet_');
            if(gss) {
                var query = gss.find('.query').text();
                var searchResultsTitle = $('#search-result-title');
                if (searchResultsTitle.length > 0) {
                    searchResultsTitle.append(' \"' + query + '\"');
                    searchResultsTitle.parent().show();
                }
            }

            if(isDesktop) {
                headerOverlay.init();
            }

            // Select2
            var liviSelect = $('select.livi-select');
            if (liviSelect) {
                liviSelect.select2({
                    minimumResultsForSearch: -1,
                    templateResult: function (item) {
                        if (item.element && item.element.className) {
                            return $('<span class="' + item.element.className + '">' + item.text + '</span>');
                        } else {
                            return item.text;
                        }
                    }
                });
                var selectionArrow = $('.select2-container .select2-selection__arrow');
                selectionArrow.find('b[role="presentation"]').hide();
                selectionArrow.append('<i class="fa fa-angle-down"></i>');
            }

            var faqArticle = $('.faq-accordion-article');
            if (faqArticle) {
                $('.faq-question').on('click', function(){
                    $(this).parent().toggleClass('active');
                });
            }

            var newsletterForm = $(".newsletter-form");
            if(newsletterForm) {
                newsletter.init(newsletterForm);
            }

            var newsHighlightContent = $(".news-content-text .content");
            if(newsHighlightContent) {
                newsHighlightContent.dotdotdot();
            }

            var projectHighlightListText = $(".card-text .content");
            if(projectHighlightListText) {
                projectHighlightListText.dotdotdot();
            }

            nanogallery.init();

            var contentPageSideNavigation = $(".nav-menu");
            if(contentPageSideNavigation) {

                // init navigation open status
                $(".nav-menu li > ul").addClass("hidden");
                var openListItem = $(".nav-menu li.open > ul"); 
                openListItem.removeClass("hidden");

                // init button state
                $(".nav-menu .side-navigation-button").addClass("closed");
                var openListButton = $(".nav-menu li.open > .side-navigation-button"); 
                openListButton.addClass("open").removeClass("closed");

                // handle button click
                $('.nav-menu .side-navigation-button').on('click', function(){
                    $(this).next("ul").toggleClass("hidden");
                    
                    if($(this).next("ul").hasClass("hidden")) {
                        $(this).removeClass("open").addClass("closed");
                    } else {
                        $(this).removeClass("closed").addClass("open");
                    }
                });
            }

            // Remove min height from empty column unsigned users
            var emptyLayoutColumn = $(".unsigned .portlet-column-content.empty");
            if(emptyLayoutColumn) {
                emptyLayoutColumn.parent('[class*="span"]').css("min-height", "0px");
            }

            // Multi-categories select
            var $mcsWrapper = $(".multi-categories-select");
            if ($mcsWrapper.length) {
                var baseUrl = $mcsWrapper.data("baseurl");
                var placeholder = $mcsWrapper.data("placeholder");
                if (baseUrl && placeholder) {
                    var $selects = $mcsWrapper.find("select");
                    $selects.on("change", function () {
                        var categoryIds = "";
                        $selects.each(function () {
                            var $select = $(this);
                            if ($select.val()) {
                                categoryIds += $select.val() + ",";
                            }
                        });
                        window.location = baseUrl.replace(placeholder, categoryIds);
                    });
                }
            }
        };
    }( window.theme = window.theme || {}, jQuery ));

    theme.init();
});

(function() {
    window.rnsData = {
        apiKey: '9g8lvf941ier7kcw'
    };
    var s = document.createElement('script');
    s.src = 'https://cdn.reactandshare.com/plugin/rns.js';

    document.body.appendChild(s);
}());