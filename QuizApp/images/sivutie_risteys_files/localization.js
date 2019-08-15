
var langPropertiesFi = {
    newsletterSuccessHeader : "Uutiskirje tilattu onnistuneesti",
    newsletterSuccessContentHeading : "Muista vahvistaa tilaus",
    newsletterSuccessContentInfo : "Mikäli olet uusi tilaaja, palvelumme lähetti sinulle vahvistusviestin antamaasi sähköpostiosoitteeseen. Jotta tilaus aktivoituu, sinun tulee vielä napsauttaa sähköpostiviestissä olevaa linkkiä.",
    newsletterSuccessContentConfirm : "Vahvistusviestillä varmistamme, ettei kukaan voi tilata sähköpostia toisen henkilön nimissä.",
    newsletterSuccessContentRegisterLink : "http://www.emaileri.fi/forms/comp/1203/rekisteriseloste.html",
    newsletterSuccessContentRegister : "Rekisteriseloste",
    newsletterSuccessProjectContent : "Uutiskirje tilattiin onnistuneesti.",
    newsletterErrorHeader : "Virhe",
    newsletterErrorContent : "Valitettavasti uutiskirjeen tilaaminen ei onnistunut, yritä myöhemmin uudelleen.",
    newsletterInvalidEmailContent : "Antamasi sähköposti on virheellinen.",
    nanogalleryNext : "Seuraava",
    nanogalleryPrevious : "Edellinen",
    nanogalleryBreadcrumbHome : "Galleria",
    nanogalleryCaption : "Katso Flickr-albumi"
};

// Newsletter not used in en site at the moment
var langPropertiesEn = {
    newsletterSuccessHeader : "(en) Uutiskirje tilattu onnistuneesti",
    newsletterSuccessContentHeading : "(en) Muista vahvistaa tilaus",
    newsletterSuccessContentInfo : "(en) Mikäli olet uusi tilaaja, palvelumme lähetti sinulle vahvistusviestin antamaasi sähköpostiosoitteeseen. Jotta tilaus aktivoituu, sinun tulee vielä napsauttaa sähköpostiviestissä olevaa linkkiä.",
    newsletterSuccessContentConfirm : "(en) Vahvistusviestillä varmistamme, ettei kukaan voi tilata sähköpostia toisen henkilön nimissä.",
    newsletterSuccessContentRegisterLink : "(en) http://www.emaileri.fi/forms/comp/1203/rekisteriseloste.html",
    newsletterSuccessContentRegister : "(en) Rekisteriseloste",
    newsletterSuccessProjectContent : "(en) Uutiskirje tilattiin onnistuneesti.",
    newsletterErrorHeader : "(en) Virhe",
    newsletterErrorContent : "(en) Valitettavasti uutiskirjeen tilaaminen ei onnistunut, yritä myöhemmin uudelleen.",
    newsletterInvalidEmailContent : "(en) Antamasi sähköposti on virheellinen.",
    nanogalleryNext : "Next",
    nanogalleryPrevious : "Previous",
    nanogalleryBreadcrumbHome : "Gallery",
    nanogalleryCaption : "See Flickr album"
};

// Newsletter not used in sv site at the moment
var langPropertiesSv = {
    newsletterSuccessHeader : "(sv) Uutiskirje tilattu onnistuneesti",
    newsletterSuccessContentHeading : "(sv) Muista vahvistaa tilaus",
    newsletterSuccessContentInfo : "(sv) Mikäli olet uusi tilaaja, palvelumme lähetti sinulle vahvistusviestin antamaasi sähköpostiosoitteeseen. Jotta tilaus aktivoituu, sinun tulee vielä napsauttaa sähköpostiviestissä olevaa linkkiä.",
    newsletterSuccessContentConfirm : "(sv) Vahvistusviestillä varmistamme, ettei kukaan voi tilata sähköpostia toisen henkilön nimissä.",
    newsletterSuccessContentRegisterLink : "(sv) http://www.emaileri.fi/forms/comp/1203/rekisteriseloste.html",
    newsletterSuccessContentRegister : "(sv) Rekisteriseloste",
    newsletterSuccessProjectContent : "(sv) Uutiskirje tilattiin onnistuneesti.",
    newsletterErrorHeader : "(sv) Virhe",
    newsletterErrorContent : "(sv) Valitettavasti uutiskirjeen tilaaminen ei onnistunut, yritä myöhemmin uudelleen.",
    newsletterInvalidEmailContent : "(sv) Antamasi sähköposti on virheellinen.",
    nanogalleryNext : "Nästa",
    nanogalleryPrevious : "Föregående",
    nanogalleryBreadcrumbHome : "Galleri",
    nanogalleryCaption : "Se Flickr-albumet"
};


var langProperties = langPropertiesFi;
if(locale == "en_US") {
    langProperties = langPropertiesEn;
} else if(locale == "sv_SE") {
    langProperties = langPropertiesSv;
}