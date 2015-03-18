package com.sopra.team1723.ctrl;

import org.json.simple.*;

import com.sopra.team1723.data.Benutzer;

public class JSONConverter
{
    /**
     * JSON-Feld-Werte
     */
    
    static private final String jsonErrorTxt = "error";
    static public final String jsonErrorNoError = "noerror";                        // Kein Fehler

    // Alle bekannten Fehlertypen
    static public final String jsonErrorSystemError = "systemerror";                // Ein Interner Fehler ist aufgetreten
    static public final String jsonErrorInvalidParam = "invalidparam";              // Allgemeiner Fehler. Die �bergebene Parameter sind unbekannt oder es fehlen Parameter
    static public final String jsonErrorNotLoggedIn = "notloggedin";                // Der Benutzer ist nicht eingeloggt und hat deshalb nicht die ben�tigen Rechte
    static public final String jsonErrorLoginFailed = "loginfailed";                // Login ist fehlgeschlagen. Email existiert nicht oder Passwort ist falsch.  
    static public final String jsonErrorRegisterFailed = "registerfailed";          // Allgemeiner Fehler beim Registreren
    static public final String jsonErrorEmailAlreadyInUse = "emailalreadyinuse";    // Fehler beim Registieren. Email-Adresse schon vergeben
    static public final String jsonErrorPwResetFailed = "pwresetfailed";            // Fehler beim Zur�cksetzen des Passworts

    // Benutzer 
    static public final String jsonEmail = "email";
    static public final String jsonVorname = "vorname";
    static public final String jsonNachname = "nachname";
    static public final String jsonMatrikelNr = "matrikelnr";
    static public final String jsonNutzerstatus = "nutzerstatus";
    static public final String jsonStudiengang = "studiengang";
    static public final String jsonPasswort = "passwort";
    
    /**
     * Einfache Best�tigung von Aktionen oder Error
     * @param erroText 
     * @return
     */
    static JSONObject toJsonError(String erroText)
    {
        JSONObject jo = new JSONObject();
        
        // TODO HashMap parametrisieren?
        jo.put(jsonErrorTxt, erroText);
       
        return jo;
    }
    
    /**
     * Verpackt die Daten eines Benutzer Objekts in
     * ein JSON Objekt und gibt dieses zurueck.
     * Das Passwort wird aus Sicherheitsgruenden 
     * nicht in das JSON Objekt gepackt.
     * Die Reihenfolge der Daten darf nicht veraendert werden,
     * da der Benutzer beim Client sonst falsch gelesen wird!
     * @param benutzer
     * @return JSONObject mit den Benutzerdaten
     */
    static JSONObject toJsonBenutzer(Benutzer benutzer) 
    {
        JSONObject jo = new JSONObject();
        
        // TODO HashMap parametrisieren?
        jo.put(jsonErrorTxt, jsonErrorNoError);
        jo.put(jsonEmail, benutzer.geteMail());
        jo.put(jsonVorname, benutzer.getVorname());
        jo.put(jsonNachname, benutzer.getNachname());
        jo.put(jsonMatrikelNr, new Integer(benutzer.getMatrikelnummer()).toString());
        jo.put(jsonNutzerstatus, benutzer.getNutzerstatus().toString());
        jo.put(jsonStudiengang, benutzer.getStudiengang());
        
        return jo;
    }
}
