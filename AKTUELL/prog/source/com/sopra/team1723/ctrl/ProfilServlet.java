package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.AEADBadTagException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalseLoginDataException;
import com.sopra.team1723.exceptions.DbUniqueConstraintException;

/**
 * Steuert das anzeigen und bearbeiten eines Profils.
 */
public class ProfilServlet extends ServletController
{

    /**
     * Steuert das anzeigen und bearbeiten eines Profils.
     */
    public ProfilServlet()
    {
    }

    /**
     * Aus der Request werden die neuen Proldaten ausgelesen. Diese werden
     * gepruft und wenn diese Pr�fung erfolgreich war, werden sie �ber den
     * Datenbankmanager in der DB geupdated. Gibt "true- zuruck, wenn bei diesem
     * Ablauf keine Fehler auftreten, und "false" bei Fehlern.
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean profilBearbeiten(HttpServletRequest request, HttpServletResponse response) throws IOException
    {

        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String idStr = request.getParameter(ParamDefines.Id);
        String emailNew = request.getParameter(ParamDefines.EmailNew);
        String vorname = request.getParameter(ParamDefines.Vorname);
        String nachname = request.getParameter(ParamDefines.Nachname);
        String notifyVeranstAenderung = request.getParameter(ParamDefines.NotifyVeranstAenderung);
        String notifyKarteikartenAenderung = request.getParameter(ParamDefines.NotifyKarteikartenAenderung);
        String notifyKommentare = request.getParameter(ParamDefines.NotifyKommentare);

        JSONObject jo = null;
        // Alle Parameter angegeben?
        if (isEmptyAndRemoveSpaces(emailNew) || isEmptyAndRemoveSpaces(idStr) || isEmptyAndRemoveSpaces(vorname)
                || isEmptyAndRemoveSpaces(nachname) || isEmptyAndRemoveSpaces(notifyVeranstAenderung)
                || isEmptyAndRemoveSpaces(notifyKarteikartenAenderung) || isEmptyAndRemoveSpaces(notifyKommentare))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        // HTML entfernen!
        emailNew = Jsoup.parse(emailNew).text();
        vorname = Jsoup.parse(vorname).text();
        nachname = Jsoup.parse(nachname).text();

        int id = 0;
        try
        {
            id = Integer.parseInt(idStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        // Eigene id oder andere ?
        if (id != aktuellerBenutzer.getId())
        {
            // Andere id. Bin ich also Admin?
            if (aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return false;
            }
        }

        // Boolean konvertieren
        boolean bNotifyVeranstAenderung = false;
        if (notifyVeranstAenderung.equals("true"))
        {
            bNotifyVeranstAenderung = true;
        }
        else if (notifyVeranstAenderung.equals("false"))
        {
            bNotifyVeranstAenderung = false;
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        boolean bNotifyKarteikartenAenderung = false;
        if (notifyKarteikartenAenderung.equals("true"))
        {
            bNotifyKarteikartenAenderung = true;
        }
        else if (notifyKarteikartenAenderung.equals("false"))
        {
            bNotifyKarteikartenAenderung = false;
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        // Enum konvertieren
        NotifyKommentare eNotifyKommentare;
        try
        {
            eNotifyKommentare = NotifyKommentare.valueOf(notifyKommentare);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Benutzer benutzer;
        // Sowohl wenn der Admin ein Profil �ndert, als auch der Benutzer sein
        // eigenes, m�ssen diese Exceptions abgefangen werden
        try
        {

            // Pr�fen ob dies ein Admin ist und gegebenfalls alle Parameter
            // holen
            if (aktuellerBenutzer.getNutzerstatus() == Nutzerstatus.ADMIN)
            {
                String studiengang = request.getParameter(ParamDefines.Studiengang);
                String matrikelNrStr = request.getParameter(ParamDefines.MatrikelNr);
                String nutzerstatus = request.getParameter(ParamDefines.Nutzerstatus);

                // Alle Parameter angegeben?
                if (isEmptyAndRemoveSpaces(studiengang) || isEmptyAndRemoveSpaces(matrikelNrStr)
                        || isEmptyAndRemoveSpaces(nutzerstatus))
                {
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return false;
                }
                studiengang = Jsoup.parse(studiengang).text();
                matrikelNrStr = Jsoup.parse(matrikelNrStr).text();

                // Matrikelnummer konvertieren
                int nMatrikelNr = 0;
                Nutzerstatus eNutzerstatus;
                try
                {
                    nMatrikelNr = Integer.parseInt(matrikelNrStr);
                    eNutzerstatus = Nutzerstatus.valueOf(nutzerstatus);
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return false;
                }

                benutzer = new Benutzer(id, emailNew, vorname, nachname, nMatrikelNr, studiengang, eNutzerstatus,
                        bNotifyVeranstAenderung, bNotifyKarteikartenAenderung, eNotifyKommentare);

                dbManager.bearbeiteBenutzerAdmin(benutzer, aktuellerBenutzer);

            }
            // Normaler benutzer Speichern
            else
            {
                benutzer = new Benutzer(id, emailNew, vorname, nachname, bNotifyVeranstAenderung,
                        bNotifyKarteikartenAenderung, eNotifyKommentare);
                dbManager.bearbeiteBenutzer(benutzer);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
        catch (DbUniqueConstraintException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorEmailAlreadyInUse);
            outWriter.print(jo);
            return false;
        }

        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
        outWriter.print(jo);

        return true;
    }

    /**
     * �ndert das Passwort eines Benutzers
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean passwortAendern(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        JSONObject jo = null;

        String neuesPasswort = request.getParameter(ParamDefines.PasswordNew);
        // Crypte neues Passwort:

        String cryptedNewPW = BCrypt.hashpw(neuesPasswort, BCrypt.gensalt());

        // Nutze diese Methode auch, wenn ein Admin die Email eines anderen
        // Benutzers aendert
        int benutzerId;
        try
        {
            benutzerId = Integer.parseInt(request.getParameter(ParamDefines.Id));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Benutzer user = dbManager.leseBenutzer(benutzerId);
        String benutzerMail = user.geteMail();

        try
        {
            // Eigenes Profil oder nicht ?
            if (benutzerId != aktuellerBenutzer.getId())
            {
                // Nicht eigenes Profil. Ist der Benuzer als Admin ?
                if (aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
                {
                    // wenn nein, dann error
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return false;
                }
            }
            // Eigenes Profil
            else
            {
                // Pr�fen ob altes Passwort richtig
                String altesPasswort = request.getParameter(ParamDefines.Password);
                dbManager.pruefeLogin(benutzerMail, altesPasswort);
            }
            // neues Passwort angegeben ?
            if (isEmpty(neuesPasswort))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return false;
            }
            // TODO Vllt weglassen
            if (benutzerMail == null)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return false;
            }
            // Passwort �ndern
            if (dbManager.passwortAendern(benutzerMail, cryptedNewPW))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
                outWriter.print(jo);
                return false;
            }
            else
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return false;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
        catch (DbFalseLoginDataException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorLoginFailed);
            outWriter.print(jo);
            return false;
        }

    }

    /**
     * Liest aus der Request ID und Verifikationsparameter f�r diese Handlung
     * aus. Wenn Benutzer existiert, rufe Methode zum L�schen von Benutzern des
     * Datenbankmanagers auf. Gibt "true" zur�ck, wenn bei diesem Ablauf keine
     * Fehler auftreten, und "false"bei Fehlern.
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean benutzerLoeschen(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo;

        String userId = request.getParameter(ParamDefines.Id);
        int id = 0;
        try
        {
            id = Integer.parseInt(userId);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();

            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        if (id != aktuellerBenutzer.getId() && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return false;
        }

        if (dbManager.loescheBenutzer(id))
        {
            // Eigenes profil? dann ausloggen
            if (id == aktuellerBenutzer.getId())
            {
                aktuelleSession.invalidate();
                System.out.println("test");
            }

            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }

    }

    /**
     * L�scht das profilbild des angegebenen Benutzers
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean benutzerBildLoeschen(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo;

        String userId = request.getParameter(ParamDefines.Id);
        int id = 0;
        try
        {
            id = Integer.parseInt(userId);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();

            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        if (id != aktuellerBenutzer.getId() && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return false;
        }

        if (dbManager.loescheProfilBild(id))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }

    }

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);

        if (aktuelleAction.equals(ParamDefines.ActionGetBenutzer))
        {
            jo = JSONConverter.toJson(aktuellerBenutzer, true);
            outWriter.print(jo);
            return;
        }
        else if (aktuelleAction.equals(ParamDefines.ActionGetOtherBenutzer))
        {
            String eMail = req.getParameter(ParamDefines.Email);
            Benutzer b = null;

            if (eMail != null)
                b = dbManager.leseBenutzer(eMail);
            else
            {
                String userID = req.getParameter(ParamDefines.Id);
                if (userID != null)
                {
                    try
                    {
                        int id = Integer.parseInt(userID);
                        b = dbManager.leseBenutzer(id);
                    }
                    catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                        outWriter.print(jo);
                        return;
                    }
                }
                else
                {
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam,
                            "Dieser Benutzer existiert nicht!");
                    outWriter.print(jo);
                    return;
                }
            }

            if (b == null)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam, "Dieser Benutzer existiert nicht!");
                outWriter.print(jo);
                return;
            }
            else
            {
                // TODO Testen!
                jo = JSONConverter.toJson(b, aktuellerBenutzer.getNutzerstatus() == Nutzerstatus.ADMIN);
                outWriter.print(jo);
                return;
            }
        }
        else if (aktuelleAction.equals(ParamDefines.ActionAendereProfil))
        {
            profilBearbeiten(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionSetTheme))
        {
            setTheme(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionAenderePasswort))
        {
            passwortAendern(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionDeleteBenutzer))
        {
            benutzerLoeschen(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionDeleteBenutzerBild))
        {
            benutzerBildLoeschen(req, resp);
        }
        else
        {
            // Sende Nack mit ErrorText zur�ck
            jo = null;
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }

    }

    /**
     * Setzt das Theme des Benutzers in der Datenbank
     * 
     * @param req
     * @param resp
     * @throws IOException
     */
    private void setTheme(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        JSONObject jo;

        String idStr = req.getParameter(ParamDefines.Id);
        String theme = req.getParameter(ParamDefines.Theme);

        if (isEmptyAndRemoveSpaces(theme))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        int id;
        try
        {
            id = Integer.parseInt(idStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if (id != aktuellerBenutzer.getId() && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        Benutzer b = dbManager.leseBenutzer(id);
        b.setTheme(theme);
        try
        {
            dbManager.bearbeiteBenutzer(b);
        }
        catch (DbUniqueConstraintException | SQLException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }

        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
        outWriter.print(jo);
    }
}
