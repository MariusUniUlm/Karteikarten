package com.sopra.team1723.ctrl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalseLoginDataException;
import com.sopra.team1723.exceptions.DbFalsePasswortException;
import com.sopra.team1723.exceptions.DbUniqueConstraintException;

/**
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
     * Daten des angegebenen Benutzers werden in der Datenbank geupdatet. Das Kennwort wird nicht gesetzt
     * {@link #passwortAendern(String, String)}. Diese Methode wird verwendet, wenn
     * der Admin die Daten eines Benutzers bearbeiten m�chte. Er kann dabei alle Attribute also auch
     * Matrikelnummer und Studiengang �ndern. Um den Datensatz zu identifizieren wird die ID im Benutzer Objekt
     * verwendet. Tritt ein Fehler in der Datenbank auf, dann wird eine SQLException geworfen. Wird versucht einen Benutzer
     * mit einer bereits vorhandenen eMail Adresse anzulegen, so wird eine DbUniqueConstraintException
     * geworfen
     * @param benutzer-Objket, das in der Datenbank geupdatet wird 
     * @throws DbUniqueConstraintException, SQLException
     */
    public void bearbeiteBenutzerAdmin(Benutzer benutzer) throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt den Benutzer aus der Datenbank. 
     * @param benutzerId referenziert eindeutig den zu l�schenden Benutzer
     * @return  Tritt ein Fehler auf gibt die Methode false zur�ck. Ansonsten true.
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
     * Gibt eine Liste aller Semester zur�ck. 
     * @return Liste der Semester. Tritt ein Fehler auf wird null zur�ckgegeben. Werden keine Semester in der 
     * Datenbank gefunden, dann wird eine leere Liste zur�ckgegeben
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

    //    /**
    //     * Holt alle Veranstaltungen aus der Datenbank und packt sie in eine Array List.
    //     * @return Liste aller Veranstaltungen. Gibt es keine Veranstaltungen wird eine 
    //     * leere Liste zur�ckgegeben. Bei einem Fehler kommt null zur�ck.
    //     */
    //    public List<Veranstaltung> leseAlleVeranstaltungen();
    //    
    //    /**
    //     * Holt Veranstaltungen, die von dem angegebenen Studiengang geh�rt werden k�nnen
    //     * aus der Datenbank.
    //     * @param studiengang
    //     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die 
    //     * Methode eine leere Liste zur�ck. Bei einem Fehler kommt null zur�ck.
    //     */
    //    public List<Veranstaltung> leseVeranstaltungenStudiengang(String studiengang);
    //
    //    /**
    //     * Holt alle Veranstaltungen aus dem angegebenen Semester aus der Datenbank.
    //     * @param semester 
    //     * @return Liste von Veranstaltungen. Wird keine Veranstaltung gefunden gibt die Methode eine leere Liste zur�ck.
    //     * Bei einem Fehler kommt null zur�ck.
    //     */
    //    public List<Veranstaltung> leseVeranstaltungenSemester(String semester);

    /**
     * Holt alle Veranstaltungen zu dem angegebenen Semester und Studiengang aus der Datenbank.
     * @param semester, studiengang 
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
     * @return Liste von Moderatoren der Veranstaltung.Hat die Veranstaltung  ine Moderatoren
     * wird die leere Liste zur�ckgegeben. Bei einem Fehler wird null zur�ckgegeben.
     */
    public List<Benutzer> leseModeratoren(int veranstaltung);

    /**
     * Holt alle Studiengaenge zu der angegebenen Veranstaltung aus der Datenbank.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Liste der Studieng�nge zu dieser Veranstaltung. Wird die Veranstaltung von keinem Studiengang
     * geh�rt wird die leere Liste zur�ckgegeben. Bei einem Fehler wird null zur�ckgegeben.
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
     * Holt alle Studiengaenge zu der angegebenen Veranstaltung aus der Datenbank.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung
     * @return Liste der Studieng�nge zu dieser Veranstaltung. Wird die Veranstaltung von keinem Studiengang
     * geh�rt wird die leere Liste zur�ckgegeben. Bei einem Fehler wird null zur�ckgegeben.
     */
    public boolean angemeldet(int benutzer, int veranstaltung) throws SQLException;

    /**
     * Fugt neue Veranstaltung in die Datenbank ein. Bei Erfolg wird
     * true zuruckgegeben. Bei einem Fehler in der Datenbank oder falls
     * es schon eine Veranstaltung mit dem gleichen Titel gibt, wird false
     * zuruckgeliefert.
     * @param veranst 
     * @return
     */
    public void schreibeVeranstaltung(Veranstaltung veranst, String[] studiengaenge, int[] moderatorenIds) throws SQLException, DbUniqueConstraintException;

    /**
     * Daten der angegebenen Veranstaltung werden in der Datenbank
     * geupdatet. Bei Erfolg liefert die Methode true zuruck (auch wenn
     * die Veranstaltung gar nicht in der Datenbank vorhanden war). Bei
     * einem Fehler false.
     * @param veranst 
     * @return
     */
    public void bearbeiteVeranstaltung(Veranstaltung veranst) throws SQLException, DbUniqueConstraintException;

    /**
     * Entfernt die Veranstaltung aus der Datenbank. Tritt ein Fehler auf
     * gibt die Methode false zuruck. Ansonsten true. (Auch wenn die
     * Veranstaltung gar nicht in der Datenbank vorhanden war.)
     * @param veranstTitel 
     * @return
     */
    public boolean loescheVeranstaltung(String veranstTitel);

    /**
     * 
     * Diese Methode durchsucht die Datenbank nach dem suchmuster. Die Methode ist flexibel gebaut.
     * Das hei�t, dass man in einer Liste angeben kann nach welchen Feldern in der Datenbank gesucht
     * werden soll. Die Namen der Felder sind eindeutig durch den Klassennamen und den Attributnamen bestimmt.
     * Die beiden Werte sind in der Klasse Klassenfeld gekapselt. Die Methode liefert die zum suchmuster
     * �hnlichen Ergebnisse in einer Liste zur�ck. In der Klasse ErgebnisseSuchfeld sind dabei 
     * der �hnliche Text, der Klassenname und die ID des Objekts zu dem der �hnliche Text geh�rt
     * gekapselt. In der Liste werden maximal 5 Eintr�ge gespeichert. Ist der R�ckgabewert null, so
     * ist ein Fehler aufgetreten. Gibt es kein �hnliches Feld zu dem Suchmuster, dann liefert die
     * Methode eine leere Liste zur�ck.
     * @param suchmuster nach dem Felder in der Datenbank verglichen werden
     * @param suchfeld gibt an welche Felder in der Datenbank mit dem Suchmuster verglichen werden.
     * @return Liste mit Objekten der Klasse ErgebnisseSuchfeld. Wird kein �hnliches Feld gefunden
     * gibt die Methode die leere Liste zur�ck. Bei einem Fehler wird null zur�ckgegeben.
     */
    public List<IjsonObject> durchsucheDatenbank(String suchmuster);
    
    /**
     * 
     * Diese Methode durchsucht die Datenbank nach dem suchmuster. Die Methode ist flexibel gebaut.
     * Das hei�t, dass man in einer Liste angeben kann nach welchen Feldern in der Datenbank gesucht
     * werden soll. Die Namen der Felder sind eindeutig durch den Klassennamen und den Attributnamen bestimmt.
     * Die beiden Werte sind in der Klasse Klassenfeld gekapselt. Die Methode liefert die zum suchmuster
     * �hnlichen Ergebnisse in einer Liste zur�ck. In der Klasse ErgebnisseSuchfeld sind dabei 
     * der �hnliche Text, der Klassenname und die ID des Objekts zu dem der �hnliche Text geh�rt
     * gekapselt. In der Liste werden maximal 5 Eintr�ge gespeichert. Ist der R�ckgabewert null, so
     * ist ein Fehler aufgetreten. Gibt es kein �hnliches Feld zu dem Suchmuster, dann liefert die
     * Methode eine leere Liste zur�ck.
     * @param suchmuster nach dem Felder in der Datenbank verglichen werden
     * @param suchfeld gibt an welche Felder in der Datenbank mit dem Suchmuster verglichen werden.
     * @return Liste mit Objekten der Klasse ErgebnisseSuchfeld. Wird kein �hnliches Feld gefunden
     * gibt die Methode die leere Liste zur�ck. Bei einem Fehler wird null zur�ckgegeben.
     */
    public Map<IjsonObject, Integer>  durchsucheDatenbankVeranstaltung(String suchmuster);
    
    /**
     * 
     * Diese Methode durchsucht die Datenbank nach dem suchmuster. Die Methode ist flexibel gebaut.
     * Das hei�t, dass man in einer Liste angeben kann nach welchen Feldern in der Datenbank gesucht
     * werden soll. Die Namen der Felder sind eindeutig durch den Klassennamen und den Attributnamen bestimmt.
     * Die beiden Werte sind in der Klasse Klassenfeld gekapselt. Die Methode liefert die zum suchmuster
     * �hnlichen Ergebnisse in einer Liste zur�ck. In der Klasse ErgebnisseSuchfeld sind dabei 
     * der �hnliche Text, der Klassenname und die ID des Objekts zu dem der �hnliche Text geh�rt
     * gekapselt. In der Liste werden maximal 5 Eintr�ge gespeichert. Ist der R�ckgabewert null, so
     * ist ein Fehler aufgetreten. Gibt es kein �hnliches Feld zu dem Suchmuster, dann liefert die
     * Methode eine leere Liste zur�ck.
     * @param suchmuster nach dem Felder in der Datenbank verglichen werden
     * @param suchfeld gibt an welche Felder in der Datenbank mit dem Suchmuster verglichen werden.
     * @return Liste mit Objekten der Klasse ErgebnisseSuchfeld. Wird kein �hnliches Feld gefunden
     * gibt die Methode die leere Liste zur�ck. Bei einem Fehler wird null zur�ckgegeben.
     */
    public Map<IjsonObject, Integer>  durchsucheDatenbankBenutzer(String suchmuster);

    /**
     * Liest die aktuellsten Benachrichtigungen f�r einen Benutzer aus der Datenbank
     * @param benutzer referenziert eindeutig einen Benuter
     * @param limit gibt an wie viele Benachrichtiungen f�r den Benutzer maximal ausgelesen werden 
     * @return Liste der aktuellsten Benachrichtigungen. Gibt es f�r den Benutzer keine Benachrichtigungen
     * wird eine leere Liste zur�ckgegeben. Bei einem Fehler kommt null zur�ck.
     */
    public List<Benachrichtigung> leseBenachrichtigungen(int benutzer, int limit);

    /**
     * Holt eine Benachrichtigung f�r eine Einladung als Moderator aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrEinlModerator Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zur�ck.
     */
    public BenachrEinlModerator leseBenachrEinlModerator(int id);

    /**
     * Holt eine Benachrichtigung f�r eine Karteikarten�nderung aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrEinlModerator Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zur�ck.
     */
    public BenachrKarteikAenderung leseBenachrKarteikAenderung(int id);

    /**
     * Holt eine Benachrichtigung f�r einen neuen Kommentar aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrEinlModerator Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zur�ck.
     */
    public BenachrNeuerKommentar leseBenachrNeuerKommentar(int id);

    /**
     * Holt eine Benachrichtigung f�r ein ge�ndertes Profil aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrEinlModerator Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zur�ck.
     */
    public BenachrProfilGeaendert leseBenachrProfilGeaendert(int id);

    /**
     * Holt eine Benachrichtigung f�r eine Veranstaltungs�nderung aus der Datenbank
     * @param id referenziert eindeutig eine Benachrichtigung dieses Typs 
     * @return BenachrEinlModerator Objekt. Bei einem Fehler oder wenn keine
     * Benachrichtigung gefunden wird kommt null zur�ck.
     */
    public BenachrVeranstAenderung leseBenachrVeranstAenderung(int id);

    /**
     * Schreibt die Benachrichtigung in die Datenbank
     * @param benachrichtigung Objekt. Die Methode setzt das Attribut gelesen
     * standardm��ig auf false.
     * @return Liefert bei Erfolg true zur�ck und bei einem Fehler false.
     */
    public boolean schreibeBenachrichtigung(Benachrichtigung benachrichtigung);

    /**
     * Setzt das Attribut gelesen der Benachrichtigung auf true.
     * @param benID referenziert eindeutig eine Benachrichtigung
     * @return Liefert bei Erfolg true zur�ck und bei einem Fehler false.
     */
    public boolean markiereBenAlsGelesen(int benID, int benutzerID);
    /**
     * Holt Daten der Karteikarte anhand der ID aus der Datenbank und
     * return
     * @param karteikID 
     * @return
     */
    public Karteikarte leseKarteikarte(int karteikID);

    /**
     * @param vaterKarteikID 
     * @return
     */
    public Karteikarte[] leseKindKarteikarten(int vaterKarteikID);

    /**
     * Fugt neue Karteikarte in die Datenbank ein. Bei Erfolg wird true
     * zuruckgegeben. Bei einem Fehler in der Datenbank wird false
     * zuruckgeliefert.
     * @param karteik 
     * @param vaterKarteikID 
     * @param Position 
     * @return
     */
    public boolean schreibeKarteikarte(Karteikarte karteik, int vaterKarteikID, int Position);

    /**
     * @param karteik 
     * @param sohnKarteikID
     */
    public void schreibeErsteKarteikarte(Karteikarte karteik, int sohnKarteikID);

    /**
     * Daten der angegebenen Karteikarte werden in der Datenbank geupdatet.
     * Bei Erfolg liefert die Methode true zuruck (auch wenn die
     * Karteikarte gar nicht in der Datenbank vorhanden war). Bei einem
     * Fehler false.
     * @param karteik 
     * @return
     */
    public boolean bearbeiteKarteikarte(Karteikarte karteik);

    /**
     * Entfernt die Karteikarte aus der Datenbank. Tritt ein Fehler auf
     * gibt die Methode false zuruck. Ansonsten true. (Auch wenn die
     * Karteikarte gar nicht in der Datenbank vorhanden war.)
     * @param karteikID 
     * @return
     */
    public boolean loescheKarteikarte(int karteikID);

    /**
     * Speichert die Bewertung, die der Benutzer dieser Karteikarte gegeben
     * hat. Die Gesamtbewertung der Karteikarte wird entsprechend
     * angepasst. Bei einem Fehler wird false zuruckgeliefert ansonsten
     * true.
     * @param karteikID 
     * @param bewert 
     * @param benutzer 
     * @return
     */
    public boolean bewerteKarteikarte(int karteikID, int bewert, String benutzer);

    /**
     * Gibt true zuruck, falls der Benutzer diese Karteikarte bereits bewertet
     * hat. Ansonsten wird false zuruckgegeben.
     * @param karteikID 
     * @param benutzer 
     * @return
     */
    public boolean hatKarteikarteBewertet(int karteikID, String benutzer);

    /**
     * Gibt alle Kommentare zu einer Karteikarte zuruck. Bei einem Fehler
     * wird null zuruckgegeben
     * @param karteikID 
     * @param vaterKID 
     * @return
     */
    public Kommentar[] leseKommentare(int karteikID, int vaterKID);

    /**
     * Gibt alle Kommentare zu einer Karteikarte zuruck. Bei einem Fehler
     * wird null zuruckgegeben
     * @param karteikID 
     * @param vaterKID 
     * @return
     */
    public Kommentar leseKommentar(int id);

    /**
     * Fugt neuen Kommentar in die Datenbank ein. Bei Erfolg wird
     * true zuruckgegeben. Bei einem Fehler in der Datenbank wird false
     * zuruckgeliefert.
     * @param kommentar 
     * @return
     */
    public boolean schreibeKommentar(Kommentar kommentar);

    /**
     * Daten des angegebenen Kommentars werden in der Datenbank geupdatet.
     * Bei Erfolg liefert die Methode true zuruck (auch wenn der
     * Kommentar gar nicht in der Datenbank vorhanden war). Bei einem
     * Fehler false.
     * @param kommentar 
     * @return
     */
    public boolean bearbeiteKommentar(Kommentar kommentar);

    /**
     * Entfernt den Kommentar aus der Datenbank. Tritt ein Fehler auf
     * gibt die Methode false zuruck. Ansonsten true. (Auch wenn der
     * Kommentar gar nicht in der Datenbank vorhanden war.)
     * @param kommentarID 
     * @return
     */
    public boolean loescheKommentar(int kommentarID);

    /**
     * Speichert die Bewertung, die der Benutzer diesem Kommentar gegeben
     * hat. Die Gesamtbewertung des Kommentars wird entsprechend
     * angepasst. Bei einem Fehler wird false zuruckgeliefert ansonsten
     * true.
     * @param kommentarID 
     * @param bewert 
     * @param benutzer 
     * @return
     */
    public boolean bewerteKommentar(int kommentarID, int bewert, String benutzer);

    /**
     * Gibt true zuruck, falls der Benutzer diesen Kommentar bereits bewertet
     * hat. Ansonsten wird false zuruckgegeben.
     * @param kommentarID 
     * @param benutzer 
     * @return
     */
    public boolean hatKommentarBewertet(int kommentarID, String benutzer);

    /**
     * Gibt true zuruck, falls der Benutzer ein Moderator dieser Veranstaltung
     * ist. Ansonsten false.
     * @param veranst 
     * @param benutzerMail 
     * @return
     */
    public boolean istModerator(Veranstaltung veranst, String benutzerMail);

    /**
     * Schreibt Benutzer in die Veranstaltung ein. Ist der Benutzer bereits in die Veranstaltung eingetragen,
     * wird eine DbUniqueConstraintException geworfen. Ist das Passwort f�r die angegebene Veranstaltung falsch,
     * wird eine DbFalsePasswortException geworfen. Tritt ein anderer Fehler auf wird eine SQLException
     * geworfen. Gibt es die Veranstaltung oder den Benutzer nicht, passiert in der Datenbank gar nichts.
     * Es wird in dem Fall aber auch keine Exception geworfen.
     * @param veranstaltung referenziert eindeutig eine Veranstaltung 
     * @param benutzer referenziert eindeutig einen Benutzer 
     * @param kennwort f�r den Zugang zur Veranstaltung
     * @throws SQLException, DbUniqueConstraintException, DbFalsePasswortException
     */
    public void zuVeranstaltungEinschreiben(int veranstaltung, int benutzer, String kennwort) throws SQLException, 
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
     * @param erstellerEMail 
     * @param karteikID 
     * @return
     */
    public Notiz[] leseNotizen(String erstellerEMail, int karteikID);

    /**
     * @param notiz 
     * @return
     */
    public boolean schreibeNotiz(Notiz notiz);

    /**
     * @param notiz 
     * @return
     */
    public boolean bearbeiteNotiz(Notiz notiz);

    /**
     * @param notizID 
     * @return
     */
    public boolean loescheNotiz(int notizID);

    /**
     * @param eMail 
     * @param status 
     * @return
     */
    public boolean rolleZuweisen(String eMail, Nutzerstatus status);

}
