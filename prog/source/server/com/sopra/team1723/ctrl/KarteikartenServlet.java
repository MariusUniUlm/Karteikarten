package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;
import com.sopra.team1723.data.Karteikarte.AttributTyp;
import com.sopra.team1723.data.Karteikarte.BeziehungsTyp;

/**
 * Verwaltet die Karteikarten
 */
public class KarteikartenServlet extends ServletController
{

    /**
     * Verwaltet die Karteikarten
     */
    public KarteikartenServlet()
    {
    }

    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die Informationen
     * bezuglich der Karteikarte gelesen und zuruckgegeben. Falls es diese
     * Karteikarte in der Datenbank nicht gibt, gibt die Methode false zuruck
     * 
     * @param request
     * @param response
     * @return
     */
    private void getKarteikarteByID(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo;

        int karteikartenID = -1;
        int vnId = -1;
        try
        {
            karteikartenID = Integer.parseInt(request.getParameter(ParamDefines.KkId));
            vnId = Integer.parseInt(request.getParameter(ParamDefines.VnId));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        Karteikarte Kk = dbManager.leseKarteikarte(karteikartenID);

        if (!pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, request, response)
                && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        if (Kk == null || Kk.getVeranstaltung() != vnId)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        Kk.setHatBewertet(dbManager.hatKarteikarteBewertet(karteikartenID, aktuellerBenutzer.getId()));
        jo = Kk.toJSON(true);

        outWriter.print(jo);
    }

    /**
     * Liefert die Vorg�nger zu der �bergebenen Karteikarten ID
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    private void getKarteikarteVorgaenger(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo;

        int karteikartenID = -1;
        int vnId = -1;
        try
        {
            karteikartenID = Integer.parseInt(request.getParameter(ParamDefines.KkId));
            vnId = Integer.parseInt(request.getParameter(ParamDefines.VnId));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if (aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN
                && !pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, request, response))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        Karteikarte Kk = dbManager.leseKarteikarte(karteikartenID);
        if (Kk == null || Kk.getVeranstaltung() != vnId)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        Map<Integer, Karteikarte> Kks = dbManager.leseVorgaenger(karteikartenID, 5);
        if (Kks == null)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

        List<Karteikarte> kk = new ArrayList<Karteikarte>(Kks.values());
        for (Karteikarte k : kk)
            k.setHatBewertet(dbManager.hatKarteikarteBewertet(k.getId(), aktuellerBenutzer.getId()));

        jo = JSONConverter.toJson(kk, true);
        outWriter.print(jo);
    }

    /**
     * Liefert die Nachfolger zu der �bergebenen Karteikarten ID
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    private void getKarteikarteNachfolger(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo;

        int karteikartenID = -1;
        int vnId = -1;
        try
        {
            karteikartenID = Integer.parseInt(request.getParameter(ParamDefines.KkId));
            vnId = Integer.parseInt(request.getParameter(ParamDefines.VnId));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if (aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN
                && !pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, request, response))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        Karteikarte Kk = dbManager.leseKarteikarte(karteikartenID);
        if (Kk == null || Kk.getVeranstaltung() != vnId)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        Map<Integer, Karteikarte> Kks = dbManager.leseNachfolger(karteikartenID, 5);
        if (Kks == null)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }

        List<Karteikarte> kk = new ArrayList<Karteikarte>(Kks.values());
        for (Karteikarte k : kk)
        {
            k.setHatBewertet(dbManager.hatKarteikarteBewertet(k.getId(), aktuellerBenutzer.getId()));
        }
        jo = JSONConverter.toJson(kk, true);
        outWriter.print(jo);
    }

    /**
     * Aus der Datenbank wird die gewunschte Karteikarte gelesen. Der Benutzer
     * kann diese nun mit einer Zahl bewerten. die Bewertung wird dann in der
     * Datenbank gespeichert.Falls es diese Karteikarte in der Datenbank nicht
     * gibt, gibt die Methode false zuruck
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private void karteikarteBewerten(HttpServletRequest request, HttpServletResponse response, int bewertung)
            throws IOException
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String karteikIdString = request.getParameter(ParamDefines.Id);

        if (isEmpty(karteikIdString))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        int karteikId;
        try
        {
            karteikId = Integer.parseInt(karteikIdString);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        // Rechte pruefen
        if (aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN
                && !pruefeFuerVeranstDerKarteikEingeschrieben(karteikId, request, response))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        Karteikarte karteikarte = dbManager.leseKarteikarte(karteikId);
        Veranstaltung veranstaltung = dbManager.leseVeranstaltung(karteikarte.getVeranstaltung());
        if (!veranstaltung.isBewertungenErlaubt())
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        if (dbManager.bewerteKarteikarte(karteikId, bewertung, aktuellerBenutzer.getId()))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

    }

    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die Informationen
     * bezuglich der Karteikarte gelesen und zuruckgegeben. Der Benutzer kann
     * diese nun andern und die geanderten Daten werden in der Datenbank
     * abgespeichert.Falls es diese Karteikarte in der Datenbank nicht gibt,
     * gibt die Methode false zuruck
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean karteikarteBearbeiten(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;

        try
        {
            String titel = req.getParameter(ParamDefines.Titel);
            String inhalt = req.getParameter(ParamDefines.Inhalt);
            String uploadedID = req.getParameter(ParamDefines.UploadID);
            int kkID = Integer.parseInt(req.getParameter(ParamDefines.Id));

            Karteikarte oldKk = dbManager.leseKarteikarte(kkID);
            Veranstaltung v = dbManager.leseVeranstaltung(oldKk.getVeranstaltung());

            if (v.getErsteKarteikarte() == kkID)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed,
                        "Sie k�nnen die oberste Karteikarte nicht bearbeiten.");
                outWriter.print(jo);
                return false;
            }

            KarteikartenTyp kkTyp;
            String typ = req.getParameter(ParamDefines.Type);
            if (typ.equals("mp4"))
            {
                kkTyp = KarteikartenTyp.VIDEO;
            }
            else if (typ.equals("jpg") || typ.equals("jepg") || typ.equals("png"))
            {
                kkTyp = KarteikartenTyp.BILD;
            }
            else if (typ.equals(""))
            {
                kkTyp = KarteikartenTyp.TEXT;
            }
            else if (typ.equals("doNothing"))
            {
                kkTyp = oldKk.getTyp();
            }
            else
                throw new Exception();

            int veranstaltung = Integer.parseInt(req.getParameter(ParamDefines.Veranstaltung));

            boolean[] bAttribute = new boolean[AttributTyp.values().length];

            String[] attribute = req.getParameter(ParamDefines.Attribute).split(",");

            for (int i = 0; i < attribute.length; ++i)
            {
                System.out.println(attribute[i]);
                bAttribute[i] = Boolean.valueOf(attribute[i]);
            }

            // Rechte pruefen
            if (!pruefeFuerVeranstDerKarteikEingeschrieben(kkID, req, resp)
                    && !istModeratorDozentOderAdmin(aktuellerBenutzer, veranstaltung, dbManager))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return false;
            }
            if (dbManager.istModerator(aktuellerBenutzer.getId(), veranstaltung)
                    && !dbManager.leseVeranstaltung(veranstaltung).isModeratorKarteikartenBearbeiten())
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return false;
            }

            String[] v_uebung = req.getParameterValues(ParamDefines.V_Uebung + "[]");
            String[] v_voraussetzung = req.getParameterValues(ParamDefines.V_Voraussetzung + "[]");
            String[] v_zusatzinfo = req.getParameterValues(ParamDefines.V_Zusatzinfo + "[]");
            String[] v_sonstiges = req.getParameterValues(ParamDefines.V_Sonstiges + "[]");

            ArrayList<Tripel<BeziehungsTyp, Integer, String>> verweise = new ArrayList<Tripel<BeziehungsTyp, Integer, String>>();
            if (v_voraussetzung != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_VORAUSSETZUNG, v_voraussetzung));
            if (v_uebung != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_UEBUNG, v_uebung));
            if (v_zusatzinfo != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_ZUSATZINFO, v_zusatzinfo));
            if (v_sonstiges != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_SONSTIGES, v_sonstiges));

            Karteikarte karteikarte = new Karteikarte(kkID, titel, inhalt, kkTyp, veranstaltung, bAttribute[0],
                    bAttribute[1], bAttribute[2], bAttribute[3], bAttribute[4], bAttribute[5], bAttribute[6],
                    bAttribute[7], bAttribute[8], bAttribute[9], verweise);

            // Kein text mehr und dateien m�ssen ge�ndert werden?

            String relativerPfad = "";
            String relativerNeuerPfad = "";
            if (kkTyp == KarteikartenTyp.VIDEO)
            {
                relativerPfad = dirKKVideo + uploadedID + ".mp4";
                relativerNeuerPfad = dirKKVideo + kkID + ".mp4";
            }
            else
            {
                relativerPfad = dirKKBild + uploadedID + ".png";
                relativerNeuerPfad = dirKKBild + kkID + ".png";
            }

            ServletContext servletContext;
            String contextPath;

            servletContext = getServletContext();
            contextPath = servletContext.getRealPath(File.separator);

            String absolutePath = contextPath + relativerPfad;
            String absoluteNeuerPfad = contextPath + relativerNeuerPfad;
            File oldName = new File(absolutePath);
            File newName = new File(absoluteNeuerPfad);

            // Datei l�schen
            if (typ.equals("") && uploadedID.equals("-1"))
            {
                // Datei mit id namen l�schen
                FileUtils.deleteQuietly(newName);
            }
            // Nichts tun
            else if (typ.equals("doNothing") && uploadedID.equals("-1"))
            {

            }
            // Datei updaten
            else
            {
                try
                {
                    FileUtils.deleteQuietly(newName);
                    if (!oldName.renameTo(newName))
                    {
                        throw new Exception();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                    outWriter.print(jo);
                }
            }

            boolean result = dbManager.bearbeiteKarteikarte(karteikarte, aktuellerBenutzer.getId());
            if (!result)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return true;
            }

            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);

        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
        }

        outWriter.print(jo);
        return true;
    }

    /**
     * Ein Benutzer erstellt eine Karteikarte und weist ihr verschiedene
     * Eigenschaften zu. Die Karteikarte bekommt eine individuelle ID. Auerdem
     * wird gepruft ob es bereits eine Karteikarte mit dem selben Inhalt gibt.
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private void erstelleKarteikarte(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;

        try
        {
            String titel = req.getParameter(ParamDefines.Titel);
            String inhalt = req.getParameter(ParamDefines.Inhalt);
            String uploadedID = "";

            KarteikartenTyp kkTyp;
            String typ = req.getParameter(ParamDefines.Type);
            if (typ.equals("mp4"))
            {
                kkTyp = KarteikartenTyp.VIDEO;
                uploadedID = req.getParameter(ParamDefines.UploadID);
            }
            else if (typ.equals("jpg") || typ.equals("png"))
            {
                kkTyp = KarteikartenTyp.BILD;
                uploadedID = req.getParameter(ParamDefines.UploadID);
            }
            else if (typ.equals(""))
            {
                kkTyp = KarteikartenTyp.TEXT;
            }
            else
                throw new Exception();

            int veranstaltung = Integer.parseInt(req.getParameter(ParamDefines.Veranstaltung));
            int vaterKK = Integer.parseInt(req.getParameter(ParamDefines.VaterKK));
            int ueberliegendeBruderKK = Integer.parseInt(req.getParameter(ParamDefines.BruderKK));

            if (vaterKK == -1 && ueberliegendeBruderKK == -1)
                throw new Exception();

            boolean[] bAttribute = new boolean[AttributTyp.values().length];

            String[] attribute = req.getParameter(ParamDefines.Attribute).split(",");

            for (int i = 0; i < attribute.length; ++i)
            {
                bAttribute[i] = Boolean.valueOf(attribute[i]);
            }

            // Rechte pruefen
            if (!pruefeFuerVeranstDerKarteikEingeschrieben(vaterKK, req, resp)
                    && !istModeratorDozentOderAdmin(aktuellerBenutzer, veranstaltung, dbManager))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return;
            }
            if (dbManager.istModerator(aktuellerBenutzer.getId(), veranstaltung)
                    && !dbManager.leseVeranstaltung(veranstaltung).isModeratorKarteikartenBearbeiten())
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return;
            }

            String[] v_uebung = req.getParameterValues(ParamDefines.V_Uebung + "[]");
            String[] v_voraussetzung = req.getParameterValues(ParamDefines.V_Voraussetzung + "[]");
            String[] v_zusatzinfo = req.getParameterValues(ParamDefines.V_Zusatzinfo + "[]");
            String[] v_sonstiges = req.getParameterValues(ParamDefines.V_Sonstiges + "[]");

            ArrayList<Tripel<BeziehungsTyp, Integer, String>> verweise = new ArrayList<Tripel<BeziehungsTyp, Integer, String>>();
            if (v_voraussetzung != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_VORAUSSETZUNG, v_voraussetzung));
            if (v_uebung != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_UEBUNG, v_uebung));
            if (v_zusatzinfo != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_ZUSATZINFO, v_zusatzinfo));
            if (v_sonstiges != null)
                verweise.addAll(konvertVerweise(BeziehungsTyp.V_SONSTIGES, v_sonstiges));

            Karteikarte karteikarte = new Karteikarte(titel, inhalt, kkTyp, veranstaltung, bAttribute[0],
                    bAttribute[1], bAttribute[2], bAttribute[3], bAttribute[4], bAttribute[5], bAttribute[6],
                    bAttribute[7], bAttribute[8], bAttribute[9], verweise);

            int kkID = dbManager.schreibeKarteikarte(karteikarte, vaterKK, ueberliegendeBruderKK);

            if (kkTyp == KarteikartenTyp.VIDEO || kkTyp == KarteikartenTyp.BILD)
            {
                String relativerPfad = "";
                String relativerNeuerPfad = "";
                if (kkTyp == KarteikartenTyp.VIDEO)
                {
                    relativerPfad = dirKKVideo + uploadedID + ".mp4";
                    relativerNeuerPfad = dirKKVideo + kkID + ".mp4";
                }
                else
                {
                    relativerPfad = dirKKBild + uploadedID + ".png";
                    relativerNeuerPfad = dirKKBild + kkID + ".png";
                }

                ServletContext servletContext;
                String contextPath;

                servletContext = getServletContext();
                contextPath = servletContext.getRealPath(File.separator);

                String absolutePath = contextPath + relativerPfad;
                String absoluteNeuerPfad = contextPath + relativerNeuerPfad;
                File oldName = new File(absolutePath);
                File newName = new File(absoluteNeuerPfad);
                try
                {
                    if (!oldName.renameTo(newName))
                    {
                        throw new Exception();
                    }
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                    outWriter.print(jo);
                }

            }

            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            jo.put(ParamDefines.Id, kkID); // Schicke die karteikarten id
                                           // zur�ck!

        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
        }

        outWriter.print(jo);
    }

    /**
     * Konvertiert die Verweise eines Typs in ein tripel aus typ, id und name.
     * 
     * @param typ
     * @param zielIds
     * @return
     * @throws IllegalArgumentException
     */
    private ArrayList<Tripel<BeziehungsTyp, Integer, String>> konvertVerweise(BeziehungsTyp typ, String[] zielIds)
            throws IllegalArgumentException
    {
        ArrayList<Tripel<BeziehungsTyp, Integer, String>> verweise = new ArrayList<Tripel<BeziehungsTyp, Integer, String>>();
        for (int i = 0; i < zielIds.length; ++i)
        {
            verweise.add(new Tripel<BeziehungsTyp, Integer, String>(typ, Integer.valueOf(zielIds[i]), ""));
        }
        return verweise;
    }

    /**
     * Pr�ft ob der aktuelle Benutzer admin dozent oder Moderator in einer
     * veranstaltung ist.
     * 
     * @param aktuellerBenutzer
     * @param veranstaltung
     * @param dbManager
     * @return
     */
    private boolean istModeratorDozentOderAdmin(Benutzer aktuellerBenutzer, int veranstaltung,
            IDatenbankmanager dbManager)
    {
        if (aktuellerBenutzer.getId() != dbManager.leseVeranstaltung(veranstaltung).getErsteller().getId()
                && !(dbManager.istModerator(aktuellerBenutzer.getId(), veranstaltung))
                && !(aktuellerBenutzer.getNutzerstatus() == Nutzerstatus.ADMIN))
        {
            return false;
        }
        return true;
    }

    /**
     * Liest die Karteikarte zur �bergebenen ID aus der Datenbank und liefert
     * sie zur�ck
     * 
     * @param req
     * @param resp
     * @throws IOException
     */
    private void leseKarteikarte(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;
        int karteikartenID = -1;
        int vnId = -1;
        try
        {
            karteikartenID = Integer.parseInt(req.getParameter(ParamDefines.KkId));
            vnId = Integer.parseInt(req.getParameter(ParamDefines.VnId));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if (!pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, req, resp)
                && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        Karteikarte Kk = dbManager.leseKarteikarte(karteikartenID);

        if (Kk == null || Kk.getVeranstaltung() != vnId)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        outWriter.print(Kk.toJSON(true));

    }

    /**
     * Liest die Kinder einer Karteikarte und gibt davon id und titel zur�ck.
     * 
     * @param req
     * @param resp
     * @throws IOException
     */
    private void leseKarteikartenKinder(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;
        int karteikartenID = -1;
        int vnId = -1;
        try
        {
            karteikartenID = Integer.parseInt(req.getParameter(ParamDefines.KkId));
            vnId = Integer.parseInt(req.getParameter(ParamDefines.VnId));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if (!pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, req, resp)
                && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        Karteikarte Kk = dbManager.leseKarteikarte(karteikartenID);

        if (Kk == null || Kk.getVeranstaltung() != vnId)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        Map<Integer, Tupel<Integer, String>> kindKarteikarten = dbManager.leseKindKarteikarten(karteikartenID);

        if (kindKarteikarten == null)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }

        jo = JSONConverter.toJsonKarteikarten(kindKarteikarten);
        outWriter.print(jo);

    }

    /**
     * K�scht die Karteikarte mit der angegebenen ID
     * 
     * @param req
     * @param resp
     * @throws IOException
     */
    private void loescheKk(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;
        int karteikartenID = -1;
        try
        {
            karteikartenID = Integer.parseInt(req.getParameter(ParamDefines.Id));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        Karteikarte k = dbManager.leseKarteikarte(karteikartenID);
        Veranstaltung v = dbManager.leseVeranstaltung(k.getVeranstaltung());

        if (v.getErsteKarteikarte() == karteikartenID)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed,
                    "Sie k�nnen die oberste Karteikarte nicht l�schen.");
            outWriter.print(jo);
            return;
        }

        // Rechte pruefen
        if (!(dbManager.istModerator(aktuellerBenutzer.getId(), v.getId()) && v.isModeratorKarteikartenBearbeiten())
                && v.getErsteller().getId() != aktuellerBenutzer.getId()
                && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        // Wenn das l�schen erfolgreich war, dann wird die Datei (bild/video)
        // ebenfalls entfernt
        if (dbManager.loescheKarteikarte(karteikartenID))
        {
            String relativerPfad = "";
            if (k.getTyp() == KarteikartenTyp.VIDEO)
            {
                relativerPfad = dirKKVideo + karteikartenID + ".mp4";
            }
            else
            {
                relativerPfad = dirKKBild + karteikartenID + ".png";
            }

            ServletContext servletContext;
            String contextPath;

            servletContext = getServletContext();
            contextPath = servletContext.getRealPath(File.separator);

            String absolutePfad = contextPath + relativerPfad;
            File newName = new File(absolutePfad);

            // Datei mit id namen l�schen
            FileUtils.deleteQuietly(newName);

            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
        }
        outWriter.print(jo);

    }

    /**
     * Pr�ft ob der Benutzer der eine Kartekarte laden will, zu der zugeh�rigen
     * Veranstaltung eingeschrieben ist.
     * 
     * @param karteikarte
     * @param req
     * @param resp
     * @return
     * @throws IOException
     */
    private boolean pruefeFuerVeranstDerKarteikEingeschrieben(int karteikarte, HttpServletRequest req,
            HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        try
        {
            Karteikarte kk = dbManager.leseKarteikarte(karteikarte);
            if (kk == null)
            {
                return false;
            }
            return dbManager.angemeldet(aktuellerBenutzer.getId(), kk.getVeranstaltung());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exportiert das Skript der angegebenen Veranstaltung. Diese Anfrage kann
     * unter umst�nden l�nger dauern, da das latex dokument erst erzeugt und
     * dann kompiliert werden muss.
     * 
     * @param req
     * @param resp
     * @throws IOException
     */
    private void exportSkript(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;

        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(File.separator);

        String[] options = req.getParameterValues(ParamDefines.ExportOptions + "[]");
        if (options == null)
        {   
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        int vnId = -1;
        try
        {
            vnId = Integer.parseInt(req.getParameter(ParamDefines.Id));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        Veranstaltung v = dbManager.leseVeranstaltung(vnId);
        String PDFPfad = "pdfExports";

        // Pr�fen, ob noch alte dateien erzeugt wurden
        PDFExporter pexp = (PDFExporter) aktuelleSession.getAttribute(sessionAttributePDFExporter);
        System.out.println("Last exporter: " + pexp);
        if (pexp != null)
            pexp.deleteFiles();

        pexp = new PDFExporter(contextPath + "/" + PDFPfad, contextPath, v, Boolean.valueOf(options[0]),
                Boolean.valueOf(options[1]), Boolean.valueOf(options[2]));

        // Tiefe = -1 sorgt daf�r, dass die oberste KK �bersprungen wird.
        appendKKExportRecursive(dbManager, pexp, -1, v.getErsteKarteikarte(), aktuellerBenutzer.getId());
        pexp.startConvertToPDFFile();

        // Warten bis fertig
        while (!pexp.getExecutor().creationFinished())
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {}
        }
        System.out.println("Fertig.");
        if (pexp.getExecutor().creationSucessfull())
        {
            System.out.println("Erfolgreich.");
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            jo.put(ParamDefines.PDFFileName, PDFPfad + "/" + pexp.getPDFFileName());
            jo.put(ParamDefines.TexFileName, PDFPfad + "/" + pexp.getTexFileName());

            // In session speichern
            aktuelleSession.setAttribute(sessionAttributePDFExporter, pexp);
        }
        else
        {
            System.out.println("nicht erfolgreich.");
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError,
                    "Fehler beim Erzeugen der PDF. Bitte versuchen Sie es erneut.");
            jo.put(ParamDefines.TexFileName, PDFPfad + "/" + pexp.getTexFileName());
        }

        outWriter.print(jo);
    }
    /**
     * F�gt eine Karteikarte zum Exporter hinzu. Diese funktion l�uft rekursiv durch den Karteikarten baum durch.
     * @param dbManager
     * @param pexp
     * @param depth
     * @param vaterKkId
     * @param benutzerId
     */
    private void appendKKExportRecursive(IDatenbankmanager dbManager, PDFExporter pexp, int depth, int vaterKkId,
            int benutzerId)
    {
        // Vater hinzuf�gen
        Karteikarte k = dbManager.leseKarteikarte(vaterKkId);

        if (k == null)
            return;

        Notiz n = dbManager.leseNotiz(benutzerId, k.getId());
        pexp.appendKarteikarte(k, depth, n);

        Map<Integer, Tupel<Integer, String>> kkList = dbManager.leseKindKarteikarten(vaterKkId);
        if (kkList == null)
            return;

        int i = 0;
        for (; i < kkList.size(); i++)
        {
            Tupel<Integer, String> tu = kkList.get(i);
            appendKKExportRecursive(dbManager, pexp, depth + 1, tu.x, benutzerId);
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

        if (aktuelleAction.equals(ParamDefines.ActionGetKarteikartenKinder))
        {
            leseKarteikartenKinder(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionGetKarteikarteByID))
        {
            getKarteikarteByID(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionErstelleKarteikarte))
        {
            erstelleKarteikarte(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionBearbeiteKarteikarte))
        {
            karteikarteBearbeiten(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionGetKarteikartenNachfolger))
        {
            getKarteikarteNachfolger(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionGetKarteikartenVorgaenger))
        {
            getKarteikarteVorgaenger(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionVoteKarteikarteUp))
        {
            karteikarteBewerten(req, resp, 1);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionVoteKarteikarteDown))
        {
            karteikarteBewerten(req, resp, -1);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionExportSkript))
        {
            exportSkript(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionDeleteKk))
        {
            loescheKk(req, resp);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);

        }
    }

}
