

package com.sopra.team1723.ctrl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalseLoginDataException;
import com.sopra.team1723.exceptions.DbFalsePasswortException;
import com.sopra.team1723.exceptions.DbUniqueConstraintException;

/**
 * @author Matthias Englert
 * Interface zur Datenbank
 */
public interface IDatenbankmanager {

    /**
     * Liest Benutzer mit der angegebenen Email aus der Datenbank.
     * @param eMail referenziert eindeutig einen Benutzer 
     * @return Gibt Benutzer zur�ck. Wird kein Benutzer gefunden oder
     * tritt ein Fehler auf liefert die Methode null zur�ck
     */
    public Benutzer leseBenutzer(String eMail);

    /**
     * Liest Benutzer mit der angegebenen id aus der Datenbank.
     * @param id referenziert eindeutig einen Benutzer 
     * @return Gibt Benutzer zur�ck. Wird kein Benutzer gefunden oder
     * tritt ein Fehler auf liefert die Methode null zur�ck
     */
    public Benutzer leseBenutzer(int id);

    /**
     * Legt neuen Benutzer in der Datenbank an. Tritt ein Fehler in der 
     * Datenbank auf, dann wird eine SQLException geworfen.
     * Wird versucht einen Benutzer mit einer bereits vorhandenen 
     * eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer-Objket, das in der Datenbnank gespeichert wird 
     * @throws DbUniqueConstraintException, SQLException
     */
    public void schreibeBenutzer(Benutzer benutzer)  throws DbUniqueConstraintException, SQLException;

    /**
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Kennwort, Matrikelnummer, 
     * Studiengang und Nutzerstatus werden nicht gesetzt, da der Benutzer diese nicht selbst �ndern kann.
     * Die eMail kann auch ge�ndert werden. Um den Datensatz zu identifizieren wird die ID im Benutzer Objekt verwendet. 
     * Das Kennwort kann in einer separaten Methode bearbeitet werden{@link #passwortAendern(String, String)}.
     * Matrikelnummer und Studiengang kann ein Benutzer nicht selber bearbeiten. Tritt ein Fehler in der Datenbank 
     * auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer mit einer bereits vorhandenen 
     * eMail Adresse anzulegen, so wird eine DbUniqueConstraintException geworfen
     * @param benutzer-Objket, das in der Datenbank geupdatet wird
     * @throws DbUniqueConstraintException, SQLException
     */
    public void bearbeiteBenutzer(Benutzer benutzer) throws SQLException, DbUniqueConstraintException;

    /**
     * �ndert das Profilbild eines Benutzers in der Datenbank.
     * @param benutzerId referenziert eindeutig den Benutzer
     * @param dateiName, Dateiname des neuen Profilbilds
     * @return true, falls kein Fehler auftritt, false bei einem Fehler
     */
    public boolean aendereProfilBild(int benutzerId, String dateiName);
    
    /**
     * L�scht das Profilbild eines Benutzers in der Datenbank.
     * @param benutzerId referenziert eindeutig den Benutzer
     * @return true, falls kein Fehler auftritt, false bei einem Fehler
     */
    public boolean loescheProfilBild(int benutzerId);
    
    /**
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Das Kennwort wird nicht gesetzt
     * {@link #passwortAendern(String, String)}. Diese Methode wird verwendet, wenn
     * der Admin die Daten eines Benutzers bearbeiten m�chte. Er kann dabei alle Attribute also auch
     * Matrikelnummer und Studiengang �ndern. Um den Datensatz zu identifizieren wird die ID im Benutzer Objekt
     * verwendet. Tritt ein Fehler in der Datenbank auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer
     * mit einer bereits vorhandenen eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer-Objket, das in der Datenbank geupdatet wird
     * @param admin, der das Profil des Benutzers bearbeitet
     * @throws DbUniqueConstraintException, SQLException
     */
    public void bearbeiteBenutzerAdmin(Benutzer benutzer, Benutzer admin) throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt den Benutzer aus der Datenbank. 
     * @param benutzerId referenziert eindeutig den zu l�schenden Benutzer
     * @return  Tritt ein Fehler auf, gibt die Methode false zur�ck. Ansonsten true.
     * (Auch wenn der Benutzer gar nicht in der Datenbank vorhanden war.)
     */
    public boolean loescheBenutzer(int benutzerId);

    /**
     * �berpr�ft, ob eMail und Passwort zusammenpassen. 
     * Tritt ein Fehler in der Datenbank auf, dann wird eine
     * SQLException geworfen. Wenn kein Benutzer zu der angegebenen
     * eMail und Passwort gefunden wird, wird eine DbFalseLoginDataException geworfen
     * @param eMail referenziert eindeutig den Benutzer
     * @param passwort des Benutzers der sich einloggen will.
     * @throws SQLException, DbFalseLoginDataException
     */
    public void pruefeLogin(String eMail, String passwort) throws SQLException, DbFalseLoginDataException;

    /**
     * Gibt eine Liste aller Studieng�nge zur�ck. 
     * @return Liste der Studiengaenge. Tritt ein Fehler auf wird null zur�ckgegeben. Sind keine Studieng�nge in der 
     * Datenbank vorhanden, dann wird eine leere Liste zur�ckgegeben
     */
    public List<String> leseStudiengaenge();

    /**
     * Holt alle Semester aus der Datenbank. 
     * @return Map aus einem Integer, der die Reihenfolge angibt, und dem Semester.
     * Tritt ein Fehler auf wird null zur�ckgegeben. Werden keine Semester in der 
     * Datenbank gefunden, dann wird eine leere Map zur�ckgegeben
     */
    public Map<Integer,String> leseSemester();

    /**
     * �ndert das Passwort des angegebenen Benutzers. 
     * @param eMail referenziert eindeutig den Benutzer
     * @param neuesPasswort 
     * @return Liefert bei Erfolg true zur�ck. (Auch wenn der Benutzer gar nicht in der Datenbank
     * vorhanden ist.) Bei einem Fehler wird false zur�ckgegeben.
     */
    public boolean passwortAendern(String eMail , String neuesPasswort );

    /**
     * Holt Veranstaltung mit der id des Parameters aus der Datenbank.
     * @param id referenziert eindeutig eine Veranstaltung 
     * @return Veranstaltung mit der angegebenen id. Ist keine Veranstaltung mit diesem Titel 
     * vorhanden oder tritt ein Fehler auf, wird null zur�ckgegeben.
     */
    public Veranstaltung leseVeranstaltung(int id);

    /**
     * Holt alle Veranstaltungen zu dem angegebenen Semester und Studiengang aus der Datenbank.
     * @param semester referenziert eindeutig ein Semester
     * @param studiengang referenziert eindeutig einen Studiengang
     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die Methode eine leere Liste zur�ck.
     * Bei einem Fehler kommt null zur�ck.    
    */
    public List<Veranstaltung> leseVeranstaltungen(String semester, String studiengang);

    /**
     * Holt alle Veranstaltungen des angegebenen Benutzers.
     * @param benutzer referenziert eindeutig einen Benutzer 
     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die Methode eine leere Liste zur�ck.
     * Bei einem Fehler kommt null zur�ck.
     */
    public List<Veranstaltung> leseVeranstaltungen(int benutzer);

    /**
     * Holt alle Moderatoren zu der angegebenen Veranstaltung aus der Datenbank.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Liste von Moderatoren der Veranstaltung. Hat die Veranstaltung  keine Moderatoren
     * wird eine leere Liste zur�ckgegeben. Bei einem Fehler wird null zur�ckgegeben.
     */
    public List<Benutzer> leseModeratoren(int veranstaltung);

    /**
     * Holt alle Studiengaenge zu der angegebenen Veranstaltung aus der Datenbank.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Liste der Studieng�nge zu dieser Veranstaltung. Wird die Veranstaltung von keinem Studiengang
     * geh�rt wird eine leere Liste zur�ckgegeben. Bei einem Fehler wird null zur�ckgegeben.
     */
    public List<String> leseStudiengaenge(int veranstaltung);
    
    /**
     * Pr�ft ob der Benutzer Moderator dieser Veranstaltung ist.
     * @param benutzer referenziert eindeutig einen Benutzer
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Gibt true zur�ck, wenn der Benutzer Moderator der Veranstaltung ist und false falls nicht.
     * Tritt ein Fehler auf wird null zur�ckgegeben
     */
    public Boolean istModerator(int benutzer, int veranstaltung);

    /**
     * Pr�ft, ob ein Benutzer in eine Veranstaltung eingeschrieben ist.
     * Bei einem Fehler wird eine SQLException geworfen
     * @param benutzer referenziert eindeutig einen Benutzer
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return true, falls eingeschrieben, false andernfalls.
     * @throws SQLException
     */
    public boolean angemeldet(int benutzer, int veranstaltung) throws SQLException;

    /**
     * F�gt eine neue Veranstaltung in die Datenbank ein.
     * Werden Titel und Semester zu bereits vorhandenen Werten ge�ndert,
     * wird eine DbUniqueConstraintException geworfen. Bei einem anderen Fehler wird
     * eine SQLException geworfen.
     * @param veranst-Objekt, das in die db eingef�gt wird
     * @param studiengaenge die zu der Veranstaltung geh�ren
     * Veranstaltung werden.
     * @param moderatorenIds bestimmen die Moderatoren der Veranstaltung
     * @return gibt die ID der soeben eingef�gten Veranstaltung zur�ck.
     * @throws SQLException, DbUniqueConstraintException
     */
    public int schreibeVeranstaltung(Veranstaltung veranst, String[] studiengaenge, int[] moderatorenIds) 
            throws SQLException, DbUniqueConstraintException;

    /**
     * Daten der angegebenen Veranstaltung werden in der Datenbank
     * geupdatet. Werden Titel und Semester zu bereits vorhandenen Werten ge�ndert,
     * wird eine DbUniqueConstraintException geworfen. Bei einem anderen Fehler wird
     * eine SQLException geworfen.
     * @param veranst-Objekt, das in der db geupdatet wird
     * @param studiengaenge zu denen die Veranstaltung geh�rt
     * @param moderatorenIds bestimmen die Moderatoren der Veranstaltung
     * @param bearbeiter referenziert eindeutig einen Benutzer. Der Parameter stellt sicher
     * dass der Bearbeiter nicht auch �ber die Veranstaltungs�nderung benachrichtigt wird
     * @throws SQLException, DbUniqueConstraintException
     */
    public void bearbeiteVeranstaltung(Veranstaltung veranst,String[] studiengaenge, int[] moderatorenIds, int bearbeiter) 
            throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt die Veranstaltung aus der Datenbank.
     * @param veranstId referenziert eindeutig eine Veranstaltung 
     * @return Liefert bei Erfolg true zur�ck und bei einem Fehler false.
     */
    public boolean loescheVeranstaltung(int veranstId);

    /**
     * Vergleicht die Titel von Veranstaltungen und die Namen von Benutzern mit dem Suchmuster
     * und gibt die 5 besten Ergebnisse zur�ck
     * @param suchmuster nach dem in der Datenbank verglichen werden
     * @return Gibt eine Map aus einem IjsonObject und einem Wert, der angibt wie gut das Suchergebnis
     * mit dem Suchmuster �bereinstimmt.
     */
    public Map<IjsonObject,Integer> durchsucheDatenbank(String suchmuster);
    
    /**
     * Vergleicht die Titel von Veranstaltungen mit dem Suchmuster und gibt die 5 besten Ergebnisse zur�ck
     * @param suchmuster nach dem Veranstaltungstitel in der Datenbank verglichen werden
     * @return Gibt eine Map aus einem IjsonObject und einem Wert, der angibt wie gut das Suchergebnis
     * mit dem Suchmuster �bereinstimmt.
     */
    public Map<IjsonObject, Integer>  durchsucheDatenbankVeranstaltung(String suchmuster);
    
    /**
     * Vergleicht die Namen von Benutzern mit dem Suchmuster und gibt die 5 besten Ergebnisse zur�ck
     * @param suchmuster nach dem Namen von Benutzern in der Datenbank verglichen werden
     * @return Gibt eine Map aus einem IjsonObject und einem Wert, der angibt wie gut das Suchergebnis
     * mit dem Suchmuster �bereinstimmt.
     */
    public Map<IjsonObject, Integer>  durchsucheDatenbankBenutzer(String suchmuster);
    
    /**
     * Durchsucht die Tabelle Studiengang nach dem Suchmuster und gibt die 5 besten Ergebnisse zur�ck
     * @param suchmuster nach dem Studieng�nge in der Datenbank verglichen werden
     * @return Gibt eine Map aus einem IjsonObject und einem Wert, der angibt wie gut das Suchergebnis
     * mit dem Suchmuster �bereinstimmt.
     */
    public Map<IjsonObject,Integer> durchsucheDatenbankStudiengang(String suchmuster);

    /**
     * Liest die aktuellsten Benachrichtigungen f�r einen Benutzer aus der Datenbank
     * @param benutzer referenziert eindeutig einen Benuter
     * @param limit gibt an wie viele Benachrichtiungen f�r den Benutzer maximal ausgelesen werden 
     * @return Liste der aktuellsten Benachrichtigungen. Gibt es f�r den Benutzer keine Benachrichtigungen
     * wird eine leere Liste zur�ckgegeben. Bei einem Fehler kommt null zur�ck.
     */
    public List<Benachrichtigung> leseBenachrichtigungen(int benutzer, int limit);
    
    /**
     * Schreibt die Benachrichtigung in die Datenbank. Bei einem Fehler wird eine
     * SQLException geworfen
     * @param benachrichtigung Objekt. Die Methode setzt das Attribut gelesen
     * standardm��ig auf false.
     * @throws SQLException
     */
    public void schreibeBenachrichtigung(Benachrichtigung benachrichtigung) throws SQLException;

    /**
     * Setzt das Attribut gelesen der Benachrichtigung auf true.
     * @param benID referenziert eindeutig eine Benachrichtigung
     * @return Liefert bei Erfolg true zur�ck und bei einem Fehler false.
     */
    public boolean markiereBenAlsGelesen(int benID, int benutzerID);
    
    /**
     * Holt Daten der Karteikarte anhand der ID aus der Datenbank
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @return Es wird ein Karteikarten Objekt zur�ckgegeben. Tritt ein Fehler
     * auf oder gibt es keine Karteikarte mit dieser ID, kommt null zur�ck
     */
    public Karteikarte leseKarteikarte(int karteikID);

    /** Gibt alle Kinder einer Karteikarte zur�ck.
     * @param vaterKarteikID referenziert eindeutig eine Karteikarte
     * @return Gibt eine Map zur�ck, in der der Schl�ssel angibt das wievielte
     * Kind die Karteikarte ist. (Es wird bei 0 mit Z�hlen begonnen)
     *  Von der Karteikarte werden nur ID und Name zur�ckgegeben. 
     * Tritt ein Fehler auf wird null zur�ckgegeben.
     * Hat die angegebene Karteikarte keine Kinder, so wird eine leere Map
     * zur�ckgeliefert.
     */
    public Map<Integer,Tupel<Integer,String>> leseKindKarteikarten(int vaterKarteikID);
    
    /**
     * Liest die Nachfolger Karteikarte einer Karteikarte.  
     * @param karteik referenziert eindeutig eine Karteikarte
     * @param anzNachfolger gibt an wie viele Nachfolger ausgelesen werden
     * @return Gibt eine Map aus Position der Karteikarte (also um den wievielten Nachfolger
     * es sich handelt) und der Karteikarte zur�ck. Bei einem Fehler wird null zur�ckgegeben.
     */
    public Map<Integer,Karteikarte> leseNachfolger(int karteikarte, int anzNachfolger);
    
    /**
     * Liest die Vorg�nger Karteikarte einer Karteikarte.  
     * @param karteik referenziert eindeutig eine Karteikarte
     * @param anzVorg�nger gibt an wie viele Vorg�nger ausgelesen werden
     * @return Gibt eine Map aus Position der Karteikarte (also um den wievielten Vorg�nger
     * es sich handelt) und der Karteikarte zur�ck. Bei einem Fehler wird null zur�ckgegeben.
     */
    public Map<Integer,Karteikarte> leseVorgaenger(int karteikarte, int anzVorg�nger);

    /**
     * F�gt neue Karteikarte in die Datenbank ein. Bei einem Fehler wird eine
     * SQLException geworfen.  
     * @param karteik-Objekt, das in der Datenbank gespeichert wird
     * @param vaterKK referenziert eindeutig eine Karteikarte. Die zu speichernde Karteikarte
     * muss immer einen Vater haben, da �ber der ersten Karteikarte keine Karteikarte eingef�gt
     * werden kann
     * @param ueberliegendeBruderKK referenziert eindeutig eine Karteikarte. Ist die Karteikarte
     * das oberste Kind einer Karteikarte, gibt es keine ueberliegendeBruderKK. In diesem Fall muss
     * der Parameter den Wert -1 haben.
     * @return ID der gerade eingef�gten Karteikarte
     * @throws SQLException
     */
    public int schreibeKarteikarte(Karteikarte karteik, int vaterKK, int ueberliegendeBruderKK) throws SQLException;

    /**
     * Daten der angegebenen Karteikarte werden in der Datenbank geupdatet.
     * Bei Erfolg liefert die Methode true zuruck (auch wenn die
     * Karteikarte gar nicht in der Datenbank vorhanden war). Bei einem
     * Fehler false.
     * @param karteik-Objekt, das in der Datenbank geupdatet wird
     * @param bearbeiter referenziert eindeutig einen Benutzer. Dieser Parameter wird ben�tigt
     * damit die Methode wei�, dass der Bearbeiter keine Benachrichtigung bekommen soll
     * @return Bei einem Fehler wird false zur�ckgeliefert ansonsten
     * true.
     */
    public boolean bearbeiteKarteikarte(Karteikarte karteik, int bearbeiter);

    /**
     * Entfernt die Karteikarte aus der Datenbank. Die L�schung erfolgt rekursiv.
     * Das hei�t, dass der komplette Unterbaum, der an dieser Karteikarte h�ngt
     * ebenfalls gel�scht wird.
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @return Tritt ein Fehler auf gibt die Methode false zur�ck. Ansonsten true. (Auch wenn die
     * Karteikarte gar nicht in der Datenbank vorhanden war.)
     */
    public boolean loescheKarteikarte(int karteikID);

    /**
     * Speichert die Bewertung, die der Benutzer dieser Karteikarte gegeben
     * hat. Die Gesamtbewertung der Karteikarte wird entsprechend
     * angepasst. 
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @param bewert, Bewertung des Benutzers, die entweder 1 oder -1 ist
     * @param benutzer referenziert eindeutig einen Benutzer
     * @return Bei einem Fehler wird false zur�ckgeliefert ansonsten
     * true.
     */
    public boolean bewerteKarteikarte(int karteikID, int bewert, int benutzer);

    /**
     * Pr�ft ob ein Benutzer eine Karteikarte bereits bewertet hat 
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @param benutzer referenziert eindeutig einen Benutzer
     * @return true, falls der Benutzer diese Karteikarte bereits bewertet
     * hat. Ansonsten wird false zur�ckgegeben.
     */
    public boolean hatKarteikarteBewertet(int karteikID, int benutzer);

    /**
     * Holt alle Themenkommentare zu einer Karteikarte
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @param aktBenutzerID referenziert eindeutig einen Benutzer. Mit dieser ID kann ermittelt
     * werden welche Kommentare dieser Benutzer bewertet hat
     * @return Liste von Themenkommentaren. Bei einem Fehler kommt null zur�ck. Gibt es keine
     * Themenkommentare zu der Karteikarte, dann wird eine leere Liste zur�ckgegeben
     */
    public ArrayList<Kommentar> leseThemenKommentare(int karteikID, int aktBenutzerID);
    
    /**
     * Holt alle Antworten zu einem Kommentar
     * @param vaterKID referenziert eindeutig einen Themenkommentar
     * @param aktBenutzerID referenziert eindeutig einen Benutzer. Mit dieser ID kann ermittelt
     * werden welche Kommentare dieser Benutzer bewertet hat
     * @return Liste von Antwortkommentaren. Bei einem Fehler kommt null zur�ck. Gibt es keine
     * Antwortkommentare zu dem Themenkommentar, dann wird eine leere Liste zur�ckgegeben
     */
    public ArrayList<Kommentar> leseAntwortKommentare(int vaterKID, int aktBenutzerID);

    /**
     * F�gt einen neuen Kommentar in die Datenbank ein
     * @param kommentar referenziert eindeutig einen Kommentar
     * @return Bei Erfolg wird true zur�ckgegeben. Bei einem Fehler in der Datenbank kommt false zur�ck
     */
    public boolean schreibeKommentar(Kommentar kommentar);

    /**
     * Entfernt den Kommentar aus der Datenbank.
     * @param kommentarID referenziert eindeutig einen Kommentar
     * @return Tritt ein Fehler auf gibt die Methode false zur�ck. Ansonsten true. (Auch wenn der
     * Kommentar gar nicht in der Datenbank vorhanden war.)
     */
    public boolean loescheKommentar(int kommentarID);

    /**
     * Speichert die Bewertung, die der Benutzer diesem Kommentar gegeben
     * hat. Die Gesamtbewertung des Kommentars wird entsprechend
     * angepasst.
     * @param kommentarID referenziert eindeutig einen Kommentar
     * @param bewert, Bewertung des Kommentars
     * @param benutzer referenziert eindeutig einen Benutzer
     * @return Bei einem Fehler wird false zur�ckgeliefert ansonsten
     * true.
     */
    public boolean bewerteKommentar(int kommentarID, int bewert, int benutzerId);

    /**
     * Pr�ft ob der Benutzer den Kommentar bereits bewertet hat
     * @param kommentarID referenziert eindeutig einen Kommentar
     * @param benutzerId referenziert eindeutig einen Benutzer
     * @return Gibt true zur�ck, falls der Benutzer den Kommentar bereits bewertet hat.
     * Falls nicht wird false zur�ckgegeben. Bei einem Fehler kommt null zur�ck.
     */
    public Boolean hatKommentarBewertet(int kommentarID, int benutzerId);

    /**
     * Schreibt Benutzer in die Veranstaltung ein. Ist der Benutzer bereits in die Veranstaltung eingetragen,
     * wird eine DbUniqueConstraintException geworfen. Ist das Passwort f�r die angegebene Veranstaltung falsch,
     * wird eine DbFalsePasswortException geworfen. Tritt ein anderer Fehler auf wird eine SQLException
     * geworfen. Gibt es die Veranstaltung oder den Benutzer nicht, passiert in der Datenbank gar nichts.
     * Es wird in dem Fall aber auch keine Exception geworfen.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung 
     * @param benutzer referenziert eindeutig einen Benutzer 
     * @param kennwort f�r den Zugang zur Veranstaltung
     * @param isAdmin. Falls isAdmin true wird nicht gepr�ft ob das Kennwort richtig ist. Der Admin darf sich also
     * in jede Veranstaltung eintragen. Ist isAdmin false, so wird die Pr�fung durchgef�hrt
     * @throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
     */
    public void zuVeranstaltungEinschreiben(int veranstaltung, int benutzer, String kennwort, boolean isAdmin) throws SQLException, 
    DbUniqueConstraintException, DbFalsePasswortException;

    /**
     * Meldet Benutzer von der Veranstaltung ab.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @param benutzer referenziert eindeutig einen Benutzer
     * @return false falls ein Fehler auftritt. Ansonsten true (Auch wenn der Benutzer gar nicht
     * zu der Veranstaltung angemeldet war)
     */
    public boolean vonVeranstaltungAbmelden(int veranstaltung, int benutzer);

    /**
     * Liest die Notiz eines Benutzers zu einer Karteikarte
     * @param benutzer referenziert eindeutig einen Benutzer 
     * @param karteikID referenziert eindeutig eine Karteikarte
     * @return Notiz-Objekt. Bei einem Fehler oder falls keine Notiz
     * gefunden wird null zur�ckgegeben.
     */
    public Notiz leseNotiz(int benutzer, int karteikID);

    /**
     * F�gt die angegebene Notiz in die Datenbank ein
     * @param notiz
     * @return Liefert true, falls kein Fehler aufgetreten ist, ansonsten false
     */
    public boolean schreibeNotiz(Notiz notiz);

    /**
     * Updatet die angegebene Notiz in der Datenbank
     * @param notiz
     * @return Liefert true, falls kein Fehler aufgetreten ist, ansonsten false
     */
    public boolean bearbeiteNotiz(Notiz notiz);

    /**
     * L�scht die Notiz aus der Datenbank
     * @param notizID referenziert eindeutig eine Notiz
     * @return Liefert true, falls kein Fehler aufgetreten ist, ansonsten false
     */
    public boolean loescheNotiz(int notizID);

    /**
     * Liest einen Kommentar aus der Datenbank
     * @param kommId referenziert eindeutig einen Kommentar 
     * @return Kommentar-Objekt. Bei einem Fehler oder falls kein Kommentar gefunden
     * wird, kommt null zur�ck
     */
    public Kommentar leseKommentar(int kommId);
    



    
}
