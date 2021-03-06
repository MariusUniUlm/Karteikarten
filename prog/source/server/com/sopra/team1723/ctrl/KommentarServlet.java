package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Verwaltet Kommentare
 */
public class KommentarServlet extends ServletController
{

    /**
     * Verwaltet Kommentare
     */
    public KommentarServlet()
    {
    }

    /**
     * Erstellt eine Antwort auf einen Kommentar
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean kommentarAntwortErstellen(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String kommIdStr = request.getParameter(ParamDefines.Id);
        String kommInhalt = request.getParameter(ParamDefines.Inhalt);

        if (isEmpty(kommIdStr) || isEmpty(kommInhalt))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int kommVaterId;
        try
        {
            kommVaterId = Integer.parseInt(kommIdStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        // Rechte pruefen
        Kommentar kommVater = dbManager.leseKommentar(kommVaterId);
        Karteikarte karteikarte = dbManager.leseKarteikarte(kommVater.getKarteikartenID());
        Veranstaltung veranstaltung = dbManager.leseVeranstaltung(karteikarte.getVeranstaltung());
        if (!veranstaltung.isKommentareErlaubt())
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return false;
        }

        Kommentar k = new Kommentar(kommInhalt, aktuellerBenutzer, kommVaterId, -1, 0);
        if (dbManager.schreibeKommentar(k))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

        return false;
    }

    /**
     * Erstellt ein neues Thema unter einer Karteikarte
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean kommentarThemaErstellen(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String kommIdStr = request.getParameter(ParamDefines.Id);
        String kommInhalt = request.getParameter(ParamDefines.Inhalt);

        if (isEmpty(kommIdStr) || isEmpty(kommInhalt))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int kommKKId;
        try
        {
            kommKKId = Integer.parseInt(kommIdStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        // Rechte pruefen
        Karteikarte karteikarte = dbManager.leseKarteikarte(kommKKId);
        Veranstaltung veranstaltung = dbManager.leseVeranstaltung(karteikarte.getVeranstaltung());
        if (!veranstaltung.isKommentareErlaubt())
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return false;
        }

        Kommentar k = new Kommentar(kommInhalt, aktuellerBenutzer, -1, kommKKId, 0);
        if (dbManager.schreibeKommentar(k))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

        return false;
    }

    /**
     * Liest alle Themen zu einer Karteikarte.
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean kommentareLeseThemen(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String kkIDStr = request.getParameter(ParamDefines.Id);

        if (isEmpty(kkIDStr))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int KKId;
        try
        {
            KKId = Integer.parseInt(kkIDStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        List<Kommentar> list = dbManager.leseThemenKommentare(KKId, aktuellerBenutzer.getId());

        JSONObject jo = JSONConverter.toJson(list, false);
        outWriter.print(jo);
        return true;
    }

    /**
     * Liest alle antworten zu einem Thema
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean kommentareLeseAntworten(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String VaterIDStr = request.getParameter(ParamDefines.Id);

        if (isEmpty(VaterIDStr))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int VaterId;
        try
        {
            VaterId = Integer.parseInt(VaterIDStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        List<Kommentar> list = dbManager.leseAntwortKommentare(VaterId, aktuellerBenutzer.getId());

        JSONObject jo = JSONConverter.toJson(list, false);
        outWriter.print(jo);
        return true;
    }

    /**
     * Bewertet einen Kommentar mit einer negativen bewertung.
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean kommentarNegativBewerten(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String kommIdStr = request.getParameter(ParamDefines.Id);

        if (isEmpty(kommIdStr))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int kommId;
        try
        {
            kommId = Integer.parseInt(kommIdStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        if (dbManager.bewerteKommentar(kommId, -1, aktuellerBenutzer.getId()))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

        return false;
    }

    /**
     * Bewertet einen Kommentar mit einer positiven Bewertung.
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean kommentarPositivBewerten(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String kommIdStr = request.getParameter(ParamDefines.Id);

        if (isEmpty(kommIdStr))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int kommId;
        try
        {
            kommId = Integer.parseInt(kommIdStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        if (dbManager.bewerteKommentar(kommId, 1, aktuellerBenutzer.getId()))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

        return false;
    }

    /**
     * L�scht den angegebenen Kommenar aus der Datenbank
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean kommentarLoeschen(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String kommIdStr = request.getParameter(ParamDefines.Id);

        if (isEmpty(kommIdStr))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int kommId;
        try
        {
            kommId = Integer.parseInt(kommIdStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Kommentar komm = dbManager.leseKommentar(kommId);
        if (komm == null)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam,
                    "Dieser Kommentar existiert nicht!");
            outWriter.print(jo);
            return false;
        }
        Kommentar vaterKomm = komm;
        if (komm.getVaterID() != -1)
            vaterKomm = dbManager.leseKommentar(komm.getVaterID());

        Karteikarte kk = dbManager.leseKarteikarte(vaterKomm.getKarteikartenID());
        Veranstaltung vn = dbManager.leseVeranstaltung(kk.getVeranstaltung());

        if (vn.getErsteller().getId() != aktuellerBenutzer.getId()
                && komm.getErsteller().getId() != aktuellerBenutzer.getId()
                && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN
                && !dbManager.istModerator(aktuellerBenutzer.getId(), kk.getVeranstaltung()))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return false;
        }

        if (dbManager.loescheKommentar(kommId))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

        return false;
    }

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        if (aktuelleAction.equals(ParamDefines.ActionVoteKommentarUp))
        {
            kommentarPositivBewerten(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionVoteKommentarDown))
        {
            kommentarNegativBewerten(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionDeleteKommentar))
        {
            kommentarLoeschen(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionErstelleAntwortKommentar))
        {
            kommentarAntwortErstellen(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionErstelleThemaKommentar))
        {
            kommentarThemaErstellen(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionLeseAntwortKommentar))
        {
            kommentareLeseAntworten(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionLeseThemaKommentar))
        {
            kommentareLeseThemen(req, resp);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }

    }
}
