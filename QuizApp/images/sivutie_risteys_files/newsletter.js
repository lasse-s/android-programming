jQuery(function ($) {
    (function( newsletter, $, undefined ) {
        var dialogHeaderClass = "newsletter-dialog-header";
        var dialogErrorIcon = "fa fa-close";
        var dialogId = "newsletter-dialog";

        newsletter.init = function(form) {
            form.submit(function(e) {
                // Simple validation for email
                if(/\S+@\S+\.\S+/.test($(this).children(".email").val())) {
                    $.ajax({
                        type: $(this).attr("method"),
                        url: $(this).attr("action"),
                        data: $(this).serialize(),

                        // TODO: Perhaps not optimal to use aui dialog in combination with jquery
                        success: function(data)
                        {
                            if(form.hasClass("project")) {
                                var bodyContent = langProperties.newsletterSuccessProjectContent;
                            } else {
                                var bodyContent =
                                    "<h3>" + langProperties.newsletterSuccessContentHeading + "</h3>"
                                    + "<p>" + langProperties.newsletterSuccessContentInfo + "</p>"
                                    + "<p>" + langProperties.newsletterSuccessContentConfirm + "</p>"
                                    + "<a href='" + langProperties.newsletterSuccessContentRegisterLink + "' target='_blank'>"
                                    + langProperties.newsletterSuccessContentRegister + "</a>";
                            }
                            AUI().use("aui-base","aui-modal", function(A) {
                                var modal = new A.Modal({
                                    bodyContent: bodyContent,
                                    centered: true,
                                    headerContent: "<h3 class='" + dialogHeaderClass + "'><i class='fa fa-check'></i>" + langProperties.newsletterSuccessHeader + "</h3>",
                                    modal: true,
                                    id: dialogId
                                }).render();
                            });
                        },
                        error:  function(data)
                        {
                            AUI().use("aui-base","aui-modal", function(A) {
                                var modal = new A.Modal({
                                    bodyContent: langProperties.newsletterErrorContent,
                                    centered: true,
                                    headerContent: "<h3 class='" + dialogHeaderClass + "'><i class='" + dialogErrorIcon + "'></i>" +langProperties.newsletterErrorHeader + "</h3>",
                                    modal: true,
                                    id: dialogId
                                }).render();
                            });
                         }
                    });
            } else {
                AUI().use("aui-base","aui-modal", function(A) {
                    var modal = new A.Modal({
                        bodyContent: langProperties.newsletterInvalidEmailContent,
                        centered: true,
                        headerContent: "<h3 class='" + dialogHeaderClass + "'><i class='" + dialogErrorIcon + "'></i>" + langProperties.newsletterErrorHeader + "</h3>",
                        modal: true,
                        id: dialogId
                    }).render();
                });
            }

            return e.preventDefault();
        });
    };
    }( window.newsletter = window.newsletter || {}, jQuery ));
});
