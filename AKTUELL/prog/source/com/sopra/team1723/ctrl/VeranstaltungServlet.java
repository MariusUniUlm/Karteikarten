package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalsePasswortException;
import com.sopra.team1723.exceptions.DbUniqueConstraintException;

/**
 * Verwaltet die Verastaltungen
 */
public class VeranstaltungServlet extends ServletController {

    /**
     * Verwaltet die Verastaltungen
     */
    public VeranstaltungServlet() {
    }

    /**
     * Ein Dozent erstellt eine Veranstaltung und weist ihr verschiedene
     * Eigenschaften zu. Die Veranstaltung bekommt eine individuelle ID.
     * Auerdem wird gepruft ob es bereits eine Veranstaltung mit dem
     * selben gibt.
     * @param request 
     * @param response 
     * @return
     * @throws IOException 
     */
    private boolean veranstaltungErstellen(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo;

        if(aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.DOZENT && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return false;
        }

        String[] studiengaenge = request.getParameter(ParamDefines.Studiengang).split(",");
        String semester = request.getParameter(ParamDefines.Semester);
        String titel = request.getParameter(ParamDefines.Titel);
        String beschr = request.getParameter(ParamDefines.Beschr);
        String zugangspasswort = request.getParameter(ParamDefines.Password);
        boolean kommentareErlaubt = Boolean.parseBoolean(request.getParameter(ParamDefines.KommentareErlauben));
        boolean bewertungenErlaubt = Boolean.parseBoolean(request.getParameter(ParamDefines.BewertungenErlauben));
        boolean moderatorKkBearbeiten = Boolean.parseBoolean(request.getParameter(ParamDefines.ModeratorKkBearbeiten));

        String[] moderatorIds = request.getParameterValues(ParamDefines.Moderatoren);
        int[] mIds = null;
        
        if(moderatorIds != null)
        {
            mIds = new int[moderatorIds.length];
            try
            {
                for(int i = 0; i < moderatorIds.length;i++)
                    mIds[i] = Integer.parseInt(moderatorIds[i]);
            }
            catch(NumberFormatException e)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return false;
            }
        }
        if(studiengaenge == null || studiengaenge.length == 0||
                isEmptyAndRemoveSpaces(semester)||
                zugangspasswort == null||
                isEmptyAndRemoveSpaces(titel)||
                isEmpty(beschr))// ||moderatorIds == null)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Veranstaltung veranst = new Veranstaltung();
        veranst.setBeschreibung(beschr);
        veranst.setTitel(titel);
        veranst.setKommentareErlaubt(kommentareErlaubt);
        veranst.setModeratorKarteikartenBearbeiten(moderatorKkBearbeiten);
        veranst.setBewertungenErlaubt(bewertungenErlaubt);
        veranst.setSemester(semester);
        veranst.setAnzTeilnehmer(0);
        veranst.setErsteller(aktuellerBenutzer);
        veranst.setZugangspasswort(zugangspasswort);


        try
        {
            veranst.setId(dbManager.schreibeVeranstaltung(veranst,studiengaenge));
        
            if(mIds != null)
            {
                for(int i = 0; i < mIds.length;i++)
                {
                    dbManager.schreibeBenachrichtigung(new BenachrEinlModerator(mIds[i], veranst));
                }
            }
        }
        catch (SQLException e)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
        catch (DbUniqueConstraintException e)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError, 
                    "Es existiert schon eine Veranstaltung \""+veranst.getTitel()+"\" im Semester \""+veranst.getSemester()+"\". "
                            + "Bitte kontaktieren Sie einen Administrator oder l�schen Sie die andere Veranstaltung, wenn Sie die Berechtigungen besitzen.");
            outWriter.print(jo);
            return false;
        } catch(NumberFormatException e)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
        outWriter.print(jo);
        return true;
    }


    private ArrayList<Boolean>leseZuWelchenVeranstAngemeldet(List<Veranstaltung> veranstaltungen, 
            HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        ArrayList<Boolean> angemeldet = new ArrayList<Boolean>();
        Iterator<Veranstaltung> it = veranstaltungen.iterator();
        while(it.hasNext()){

            try
            {
                angemeldet.add(dbManager.angemeldet(aktuellerBenutzer.getId(), it.next().getId()));
            }
            catch (SQLException e)
            {
                JSONObject jo;
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
            }
        }
        return angemeldet;
    }


    /**
     * Aus der Datenbank wird mit Hilfe der VeranstaltungsID die Informationen
     * bezuglich der Veranstaltung gelesen und zuruckgegeben.
     * Falls es diese Veranstaltung in der Datenbank nicht gibt, gibt die
     * Methode false zuruck.
     * @param request 
     * @param response 
     * @return
     * @throws IOException 
     */
    private boolean veranstaltungenAnzeigen(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);


        JSONObject jo;
        String mode = request.getParameter(ParamDefines.LeseVeranstMode);
        if(isEmpty(mode))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        else if(mode.equals(ParamDefines.LeseVeranstModeMeine))
        {
            List<Veranstaltung> verAnst = dbManager.leseVeranstaltungen(aktuellerBenutzer.getId());
            if(verAnst != null){
                ArrayList<Boolean> angemeldet = leseZuWelchenVeranstAngemeldet(verAnst, request, response);
                jo = JSONConverter.toJsonVeranstList(verAnst, angemeldet);
            }    
            else
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return true;

        }
        else if(mode.equals(ParamDefines.LeseVeranstModeStudiengangSemester))
        {
            String semester = request.getParameter(ParamDefines.GewaehltesSemester);
            String studiengang = request.getParameter(ParamDefines.GewaehlterStudiengang);
            List<Veranstaltung> verAnst = dbManager.leseVeranstaltungen(semester, studiengang);
            if(verAnst != null){
                ArrayList<Boolean> angemeldet = leseZuWelchenVeranstAngemeldet(verAnst, request, response);
                jo = JSONConverter.toJsonVeranstList(verAnst, angemeldet);
            }
            else
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return true;

        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
    }
    /**
     * Aus der Datenbank wird mit Hilfe der VeranstaltungsID die Informationen
     * bezuglich der Veranstaltung gelesen und zuruckgegeben.
     * Falls es diese Veranstaltung in der Datenbank nicht gibt, gibt die
     * Methode false zuruck.
     * @param request 
     * @param response 
     * @return
     * @throws IOException 
     */
    private boolean einzelneVeranstaltungenLesen(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo;
        String idStr = request.getParameter(ParamDefines.Id);
        if(isEmpty(idStr))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        int id;
        try
        {
            id = Integer.parseInt(idStr);            
        }
        catch (NumberFormatException e)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Veranstaltung v =  dbManager.leseVeranstaltung(id);

        if(v != null)
        {
            outWriter.print(v.toJSON(true));
            return false;
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

    }
    /**
     * Aus der Datenbank wird mit Hilfe der VeranstaltungsID die Informationen
     * bezuglich der Veranstaltung gelesen und zuruckgegeben.
     * Der Benutzer kann diese nun andern und die geanderten Daten
     * werden in der Datenbank abgespeichert.
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungBearbeiten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der VeranstaltungsID die
     * gewunschte Veranstaltung geloscht. Falls es diese Veranstaltung
     * nicht gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungLoeschen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Ein Benutzer kann sich in die gewunschte Veranstaltung einschreiben.
     * Seine ID wird dann in die betreende Datenbank bezuglich der
     * Veranstaltung geschrieben.Falls es diese Veranstaltung nicht gibt,
     * gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     * @throws IOException 
     */
    private boolean veranstaltungEinschreiben(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String idStr = request.getParameter(ParamDefines.Id);

        int vId ;
        try
        {
            vId = Integer.parseInt(idStr);

            Veranstaltung v = dbManager.leseVeranstaltung(vId); // TODO Kann das weg?

            String pw = request.getParameter(ParamDefines.Password);
            dbManager.zuVeranstaltungEinschreiben(vId, aktuellerBenutzer.getId(), pw);

            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
        }
        catch (NumberFormatException e)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        catch (SQLException e)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
        catch (DbUniqueConstraintException e)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
        }
        catch (DbFalsePasswortException e)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorLoginFailed);
            outWriter.print(jo);
            return false;
        }

        return true;
    }



    /**
     * Ein Benutzer kann sich in die gewunschte Veranstaltung ausschreiben.
     * Seine ID wird dann von der betreende Datenbank bezuglich
     * der Veranstaltung geloscht.Falls es diese Veranstaltung nicht gibt,
     * oder er noch nicht eingeschrieben ist,gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     * @throws IOException 
     */
    private boolean veranstaltungAusschreiben(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String idStr = request.getParameter(ParamDefines.Id);

        int vId;
        try
        {
            vId = Integer.parseInt(idStr);

            dbManager.vonVeranstaltungAbmelden(vId, aktuellerBenutzer.getId());

            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
        }
        catch (NumberFormatException e)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        return true;
    }

    public void leseStudiengaengeVeranstaltung(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        int veranstaltung = Integer.parseInt(req.getParameter(ParamDefines.Id));
        List<String> studiengaenge = dbManager.leseStudiengaenge(veranstaltung);
        if(studiengaenge != null)
        {
            JSONObject jo = JSONConverter.toJson(studiengaenge);
            outWriter.print(jo);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }
    }

    public void leseModeratorenVeranstaltung(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        int veranstaltung = Integer.parseInt(req.getParameter(ParamDefines.Id));
        List<Benutzer> moderatoren = dbManager.leseModeratoren(veranstaltung);
        if(moderatoren != null)
        {
            List<String> modIds = new ArrayList<String>();

            for(Benutzer b: moderatoren)
            {
                modIds.add(String.valueOf(b.getId()));
            }

            JSONObject jo = JSONConverter.toJson(modIds);
            outWriter.print(jo);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }
    }

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp) throws ServletException,
    IOException
    { 
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        if(aktuelleAction.equals(ParamDefines.ActionLeseVeranst))
        {
            veranstaltungenAnzeigen(req,resp);
        } 
        else if(aktuelleAction.equals(ParamDefines.ActionEinschreiben))
        {
            veranstaltungEinschreiben(req, resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionAusschreiben))
        {
            veranstaltungAusschreiben(req,resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionVeranstErstellen))
        {
            veranstaltungErstellen(req,resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionGetStudgVn))
        {
            leseStudiengaengeVeranstaltung(req, resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionGetModeratorenVn))
        {
            leseModeratorenVeranstaltung(req, resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionGetVeranstaltung))
        {
            einzelneVeranstaltungenLesen(req,resp);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

    }
}
