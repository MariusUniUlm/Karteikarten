/**
 * @author mk
 * @see JSONConverter.java
*/

/*
 * Zentrale Funktion,
 * die vom Server empfangene Error-Codes
 * in eine Fehlermeldung fuer den Benutzer uebersetzt.
*/
function buildMessage(errCode) {
    switch(errCode) {
        case "systemerror": return "Ein interner Serverfehler ist aufgetreten.";
        case "invalidparam": return "Der Server hat ungueltige oder fehlende Parameter erhalten.";
        case "notloggedin": return "Sie sind nicht eingeloggt.";
        case "loginfailed": return "Dieser Eintrag wurde in der Datenbank nicht gefunden oder die Datenbank ist nicht erreichbar.";
        case "registerfailed": return "Ihre Daten konnten nicht registriert werden. Bitte versuchen Sie es erneut.";
        case "emailalreadyinuse": return "Entschuldigung, diese E-Mail Adresse ist bereits vergeben.";
        case "pwresetfailed": return "Ihr Passwort konnte nicht zurueckgesetzt werden. Bitte versuchen Sie es erneut.";
    }
}