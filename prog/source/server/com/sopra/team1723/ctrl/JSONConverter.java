package com.sopra.team1723.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.*;

import com.sopra.team1723.data.BenachrEinlModerator;
import com.sopra.team1723.data.BenachrKarteikAenderung;
import com.sopra.team1723.data.BenachrNeuerKommentar;
import com.sopra.team1723.data.BenachrProfilGeaendert;
import com.sopra.team1723.data.BenachrVeranstAenderung;
import com.sopra.team1723.data.Benachrichtigung;
import com.sopra.team1723.data.Benutzer;
import com.sopra.team1723.data.Tupel;
import com.sopra.team1723.data.Veranstaltung;

/**
 * Diese Klasse konvertiert verschiedene Objekte, Listen oder Fehlermeldungen in
 * JSON-Objekte, die dann per Internet an den Client zur�ckgesendet werden.
 *
 */
@SuppressWarnings("unchecked")
public class JSONConverter
{

    /**
     * Einfache Best�tigung von Aktionen oder Error
     * 
     * @param errorCode
     * @return
     */
    static JSONObject toJsonError(String errorCode)
    {
        JSONObject jo = new JSONObject();

        jo.put(ParamDefines.jsonErrorCode, errorCode);

        return jo;
    }

    /**
     * Gibt einen Fehler inklusive individuellem ErrorText zur�ck
     * 
     * @param errorCode
     * @param errorText
     * @return
     */
    static JSONObject toJsonError(String errorCode, String errorText)
    {
        JSONObject jo = toJsonError(errorCode);
        jo.put(ParamDefines.jsonErrorMsg, errorText);
        return jo;
    }

    /**
     * Verpackt die Daten eines Benutzer Objekts in ein JSON Objekt und gibt
     * dieses zurueck. Das Passwort wird aus Sicherheitsgruenden nicht in das
     * JSON Objekt gepackt.
     * 
     * @param benutzer
     * @param full
     *            gibt an ob auch die pers. Einstellungen eingepackt werden
     *            sollen
     * @return JSONObject mit den Benutzerdaten
     */
    static JSONObject toJson(Benutzer benutzer, boolean full)
    {
        return benutzer.toJSON(full);
    }

    /**
     * 
     * @param strings
     * @return JSONObject mit einer Liste von Strings
     */
    static JSONObject toJson(List<String> strings)
    {
        JSONObject jo = new JSONObject();

        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        JSONArray array = new JSONArray();

        for (String s : strings)
            array.add(s);

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }

    /**
     * Konvertiert beliebige Listen, die das interface {@link IjsonObject}
     * implementieren in ein Json objekt
     * 
     * @param strings
     * @return JSONObject mit einer Liste von JsonObjekten
     */
    static JSONObject toJson(List<? extends IjsonObject> obj, boolean full)
    {
        JSONObject jo = new JSONObject();

        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        JSONArray array = new JSONArray();

        for (IjsonObject s : obj)
            array.add(s.toJSON(full));

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }

    /**
     * Von den Ergebnissen der Suche, welche in der Klasse ErgebnisseSuchfeld
     * gekapselt sind, werden an die GUI nur die �hnlichen Texte gegeben
     * 
     * @return JSONObject mit einer Liste von Strings
     */
    static JSONObject toJsonSuchfeld(Map<IjsonObject, Integer> suchtreffer)
    {
        JSONObject jo = new JSONObject();

        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        JSONArray array = new JSONArray();

        JSONObject tupel;
        for (Entry<IjsonObject, Integer> ergs : suchtreffer.entrySet())
        {
            tupel = new JSONObject();
            tupel.put("key", ergs.getKey().toJSON(true));
            tupel.put("value", ergs.getValue());

            array.add(tupel);
        }

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }

    /**
     * Konvertiert einen Text in ein JSON Objekt
     * 
     * @param strings
     * @return JSONObject mit NoError und einem String
     */
    static JSONObject toJson(String txt)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        jo.put(ParamDefines.jsonStrResult, txt);

        return jo;
    }

    /**
     * Konvertiert eine Liste von veranstaltungen in ein json Objekt.
     * 
     * @param veranstList
     * @param angemeldet
     *            Boolean liste, die f�r jede veranstaltung true bzw false
     *            enth�lt. Je nachdem, ob der Benutzer in dieser Veranstaltung
     *            eingeschrieben ist.
     * @return
     */
    static JSONObject toJsonVeranstList(List<Veranstaltung> veranstList, List<Boolean> angemeldet)
    {
        // TODO Interface benutzen!!
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);

        JSONArray array = new JSONArray();

        Iterator<Boolean> it = angemeldet.iterator();
        for (Veranstaltung v : veranstList)
        {
            JSONObject o = new JSONObject();

            o.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
            o.put(ParamDefines.Titel, v.getTitel());
            o.put(ParamDefines.Id, v.getId());
            o.put(ParamDefines.Beschr, v.getBeschreibung());
            o.put(ParamDefines.Semester, v.getSemester());
            o.put(ParamDefines.BewertungenErlauben, v.isBewertungenErlaubt());
            o.put(ParamDefines.ModeratorKkBearbeiten, v.isModeratorKarteikartenBearbeiten());
            o.put(ParamDefines.KommentareErlauben, v.isKommentareErlaubt());
            o.put(ParamDefines.Ersteller, JSONConverter.toJson(v.getErsteller(), true));
            o.put(ParamDefines.AnzTeilnehmer, v.getAnzTeilnehmer());
            o.put(ParamDefines.Angemeldet, it.next());

            boolean zugangsPasswortGesetzt;
            if (v.getZugangspasswort() == null)
                zugangsPasswortGesetzt = false;
            else
                zugangsPasswortGesetzt = true;

            o.put(ParamDefines.KennwortGesetzt, zugangsPasswortGesetzt);

            array.add(o);
        }

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }

    /**
     * Erzeugt ein JSON objekt aus einer Liste aus Benachrichtigungen
     * 
     * @param benachrichtigungen
     * @return
     */
    static JSONObject toJsonBenachrichtigungen(List<Benachrichtigung> benachrichtigungen)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);

        JSONArray array = new JSONArray();
        for (Benachrichtigung ben : benachrichtigungen)
        {
            JSONObject j = new JSONObject();

            j.put(ParamDefines.Id, ben.getId());
            j.put(ParamDefines.Inhalt, ben.getInhalt());
            j.put(ParamDefines.Gelesen, ben.isGelesen());
            SimpleDateFormat d = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            String s = d.format(ben.getErstelldaum().getTime());

            j.put(ParamDefines.benErstelldaum, s);

            if (ben instanceof BenachrProfilGeaendert)
            {
                BenachrProfilGeaendert b = (BenachrProfilGeaendert) ben;
                j.put(ParamDefines.Type, ParamDefines.benTypeProfil);
            }
            else if (ben instanceof BenachrEinlModerator)
            {
                BenachrEinlModerator b = (BenachrEinlModerator) ben;
                j.put(ParamDefines.Type, ParamDefines.benTypeModerator);
                j.put(ParamDefines.benVeranst, b.getVeranstaltung().toJSON(false));
            }
            else if (ben instanceof BenachrKarteikAenderung)
            {
                BenachrKarteikAenderung b = (BenachrKarteikAenderung) ben;
                j.put(ParamDefines.Type, ParamDefines.benTypeKarteikarte);
                j.put(ParamDefines.KkId, b.getKarteikarte().getId());
                j.put(ParamDefines.VnId, b.getKarteikarte().getVeranstaltung());
            }
            else if (ben instanceof BenachrNeuerKommentar)
            {
                BenachrNeuerKommentar b = (BenachrNeuerKommentar) ben;
                j.put(ParamDefines.Type, ParamDefines.benTypeKommentar);
                j.put(ParamDefines.KkId, b.getKarteikarte().getId());
                j.put(ParamDefines.VnId, b.getKarteikarte().getVeranstaltung());
            }
            else if (ben instanceof BenachrVeranstAenderung)
            {
                BenachrVeranstAenderung b = (BenachrVeranstAenderung) ben;
                j.put(ParamDefines.Type, ParamDefines.benTypeVeranstaltung);
                j.put(ParamDefines.benVeranst, b.getVeranstaltung().toJSON(false));
            }
            array.add(j);
        }
        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }

    /**
     * Um die Semester sortieren zu k�nnen, wird die ID noch mitgeliefert. Es
     * werden IDs und Name des Semesters in einer Map gespeichert. Die Methode
     * �bergibt an JavaScript keine Map sondern ein Array, bei dem in jedem
     * Eintrag sich das Paar aus ID und Name des Semesters befinden.
     * 
     * @param semester
     * @return
     */
    static JSONObject toJsonSemesterMap(Map<Integer, String> semester)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);

        JSONArray array = new JSONArray();

        for (Integer key : semester.keySet())
        {
            JSONObject o = new JSONObject();

            o.put(ParamDefines.Id, key);
            o.put(ParamDefines.Semester, semester.get(key));

            array.add(o);
        }

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }

    /**
     * Wandelt karteikarten in JSON Objekte um
     * 
     * @param karteikarten
     * @return
     */
    static JSONObject toJsonKarteikarten(Map<Integer, Tupel<Integer, String>> karteikarten)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        JSONArray array = new JSONArray();

        for (Integer key : karteikarten.keySet())
        {
            JSONObject o = new JSONObject();

            o.put(ParamDefines.Reihenfolge, key);
            o.put(ParamDefines.Id, karteikarten.get(key).x);
            o.put(ParamDefines.Titel, karteikarten.get(key).y);

            array.add(o);
        }

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }
}
