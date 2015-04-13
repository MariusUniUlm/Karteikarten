/**
 * @author mk
 * @see JSONConverter.java
*/

/**
 * Zentrale Funktion,
 * die vom Server empfangene Error-Codes
 * in eine Fehlermeldung fuer den Benutzer uebersetzt.
*/
function buildMessage(errCode) {
    switch(errCode) {
        case "systemerror": return "Ein interner Fehler ist aufgetreten. Versuchen Sie es erneut. Wenn der Fehler weiterhin besteht, wenden Sie sich an den Administrator.";
        case "invalidparam": return "Der Server hat ungueltige oder fehlende Parameter erhalten. Bitte machen Sie andere Eingaben!";
        case "notloggedin": return "Sie sind nicht eingeloggt.";
        case "loginfailed": return "Dieser Eintrag wurde in der Datenbank nicht gefunden.";
        case "registerfailed": return "Ihre Daten konnten nicht registriert werden. Bitte versuchen Sie es erneut.";
        case "emailalreadyinuse": return "Entschuldigung, diese E-Mail Adresse ist bereits vergeben.";
        case "pwresetfailed": return "Ihr Passwort konnte nicht zurueckgesetzt werden. Bitte versuchen Sie es erneut.";
        case "sessionexpired": return "Ihre Login Session ist abgelaufen. Bitte loggen Sie sich erneut ein.";
        case "notallowed": return "Diese Aktion ist nicht erlaubt. Sie haben womöglich nicht die nötigen Berechtigungen.";
        default: return "Ein unbekannter Fehler ist aufgetreten (Error Code nicht bekannt).";
    }
}