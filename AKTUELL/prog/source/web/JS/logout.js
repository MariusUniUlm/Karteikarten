/**
 * @author mk
 */

$(document).ready(function() {
    /**
     * Beim Ausloggen wird das Logout Kommando an den Server gesendet.
     * Antwortet dieser mit einem No Error wird einfach die Seite
     * neu geladen. Das darauf folgende getBenutzer erkennt,
     * dass niemand eingeloggt ist und leitet zur Startseite weiter.
     */
    $(".logout").click(function() {
        $.ajax({
            url: benutzerServlet,
            data: "action="+actionLogout,
            beforeSend: function() {
                $("#logout").off();
                $("#logout").text("Lädt...");
                $("#logout").css({"color":"gray","font-size":".75em"});
            },
            success: function(response) {
                var jsonObj = response;
                var errCode = jsonObj["error"];
                if(errCode == "noerror") {
                    location.search = "";
                } else {
                    message(0, buildMessage(errCode));
                }
            }
        });
    });
});