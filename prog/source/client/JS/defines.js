/**
 * @author Andreas, Marius
 */

var adminServlet = "AdminServlet";
var startseitenServlet = "StartseitenServlet";
var karteikartenServlet = "KarteikartenServlet";
var kommentarServlet = "KommentarServlet";
var notizServlet = "NotizServlet";
var profilServlet = "ProfilServlet";
var veranstaltungServlet = "VeranstaltungServlet";
var fileUploadServlet = "FileUploadServlet";
var suchfeldServlet = "SuchfeldServlet";
var benachrichtungsServlet = "BenachrichtigungsServlet";

var errorMsg = "errorMessage";

var actionLogin = "login";
var actionLogout = "logout";
var actionRegister = "registrieren";
var actionResetPasswort = "resetPasswort";
var actionDeleteBenutzerBild = "deleteBenutzerBild";
var actionGetBenutzer = "getBenutzer";
var actionGetStudiengaenge = "getStudiengaenge";
var actionGetStudgVn = "getStudgVn";
var actionGetModVn = "getModVn";
var actionGetOtherBenutzer = "getOtherBenutzer";
var actionGetSemester = "getSemester";
var actionGetVeranstaltung = "getVeranstaltung";
var actionAenderePasswort = "aenderePasswort";
var actionAendereProfil = "aendereProfil";
var actionUploadProfilBild = "uploadProfilBild";
var actionUploadKKBild = "uploadKKBild";
var actionUploadKKVideo = "uploadKKVideo";
var actionSucheBenVeranst = "sucheBenVeranst";
var actionSucheBenutzer = "sucheBenutzer";
var actionSucheStudiengang = "sucheStudiengang";
var actionErstelleVeranst = "erstelleVeranst";
var actionBearbeiteVeranst = "bearbeiteVeranst";
var actionDeleteBenutzer = "deleteBenutzer"; 
var actionDeleteVn = "vnLoeschen";
var actionPing = "ping";
var actionDeleteKk = "kkLoeschen";

//Liefert eine Liste von Veranstaltugen
var actionLeseVeranst = "leseVeranstaltungen";
//Liefert Veranstaltungen
var leseVeranstMode = "mode";
//Liefert alle Veranstaltungen im aktuellen Semester und studiengang
var leseVeranstModeStudiengangSemester = "studiengangSemester";
//Liefert meine Veranstaltungen
var leseVeranstModeMeine = "meine";

var actionAusschreiben = "ausschreiben";
var actionEinschreiben = "einschreiben";
var actionLeseBenachrichtungen = "leseBen";
var actionMarkiereBenGelesen = "benGelesen";

var actionGetKarteikartenVorgaenger = "getKkVor";
var actionGetKarteikartenNachfolger = "getKkNach";
var actionGetKarteikarteByID = "getKarteikarte";
var actionGetKarteikartenKinder = "getKKKinder";
var actionGetKarteikartenVater = "getKKVater";
var actionVoteKarteikarteUp = "voteUpKarteik";
var actionVoteKarteikarteDown = "voteDownKarteik";
var actionErstelleKarteikarte = "erstelleKarteikarte";
var actionBearbeiteKarteikarte = "bearbeiteKarteikarte";
var actionBearbeiteUeberschrift = "bearbeiteUeberschrift";
var actionExportSkript = "exportSkript";


var actionVoteKommentarUp = "voteUpKomm";
var actionVoteKommentarDown = "voteDownKomm";
var actionDeleteKommentar = "deleteKomm";
var actionErstelleAntwortKommentar = "erstelleAntwKomm";
var actionErstelleThemaKommentar = "erstelleThemaKomm";
var actionLeseAntwortKommentar = "leseAntwKomm";
var actionLeseThemaKommentar = "leseThemaKomm";

var actionSpeichereNotiz = "speichereNotiz";
var actionLeseNotiz = "leseNotiz";

var actionSetTheme = "setTheme";

// Allgemeines + Benutzer
var paramId = "id";
var paramEmail = "email";
var paramEmailNew = "emailNew";
var paramPasswort = "pass";
var paramPasswortNew = "passNew";
var paramVorname = "vorname";
var paramNachname = "nachname";
var paramMatrikelNr = "matrikelnr";
var paramStudiengang = "studiengang";
var paramNutzerstatus = "nutzerstatus";
var paramNofityVeranstAenderung = "notifyVeranstAenderung";
var paramNotifyKarteikartenAenderung = "notifyKarteikartenAenderung";
var paramNotifyKommentare = "notifyKommentare";
var paramNotifyKommentareValKeine = "KEINE";
var paramNotifyKommentareValVeranst = "VERANSTALTUNG_TEILGENOMMEN";
var paramNotifyKommentareValDiskussion = "DISKUSSION_TEILGENOMMEN";
var paramProfilBild = "profilBildPfad";
var paramSuchmuster = "suchmuster";
var paramInhalt = "inhalt";
var paramGelesen = "gelesen";
var paramType = "type";
var paramTheme = "theme";

//Karteikarte
var paramURLKkID = "kkId";
//Wenn mehrere IDs bertragen werden
var paramKkId = paramURLKkID;
var paramVerweise = "verweise";
var paramVerweisVoraussetzung = "V_Voraussetzung";
var paramVerweisWeiterfuehrend = "V_Zusatzinfo";
var paramVerweisUebung = "V_Uebung";
var paramVerweisSonstiges = "V_Sonstiges";
var paramKkText = "TEXT";
var paramKkBild = "BILD";
var paramKkVideo = "VIDEO";
var paramKkUploadID = "uploadID";
var paramVeranstaltung = "veranstaltung";
var paramAenderungsdatum = "aenderungsdatum";
var paramBewertung = "bewertung";
// Bei Liste von Karteikarten IDs: Index für Reihenfolge
var paramIndex = "index";
var paramAttribute = "attribute"
var paramVaterKK = "vaterKK";
var paramBruderKK = "bruderKK";

// Veranstaltungen
//Wenn mehrere IDs bertragen werden
var paramVnId = "vnId";
var paramTitel = "titel";
var paramBeschr = "beschr";
var paramSemester = "semester";
var paramBewertungenErlauben = "bewertungenErlauben";
var paramModeratorKkBearbeiten = "moderatorKkBearbeiten";
var paramKommentareErlauben = "kommentareErlauben";
var paramErsteller = "ersteller";
var paramModeratoren = "moderatoren";
var paramAnzTeilnehmer = "anzTeilnehmer";
var paramAngemeldet = "angemeldet";
var paramKennwortGesetzt = "kennwortGesetzt";
var paramErsteKarteikarte = "ersteKarteikarte";

var paramPDFFileName = "pdfFileName";
var paramTexFileName = "texFileName";
var paramExportOptions = "exportOptions";

var paramAktSemester = "aktSemester";
var paramGewaehltesStudiengang = "gewaehlterStudiengang";
var paramGewaehltesSemester = "gewaehltesSemester";

//Benachrichtigungen
var paramBenTypeModerator = "moderator";
var paramBenTypeVeranstaltung = "veranst";
var paramBenTypeKarteikarte = "karteikarte";
var paramBenTypeProfil = "profil";
var paramBenTypeKommentar = "kommentar";
var paramBenErstelldaum = "benErstelldaum";
var paramBenVeranst = "benVeranst";
var paramBenKarteikarte = "benKarteikarte";
var paramBenKommentar = "benKommentar";
var paramBenProfil = "benProfil";

// Kommentare
var paramHatGevoted = "hatgevotet";
var paramErstellDatum = "erstellDatum";
var paramAntwortCount = "antwCount";
var paramKommentarVaterId = "vaterId";
var paramKommentarKKid = "kkId";

// Schluesselnamen fuer JSON Objekte
var keyJsonStrResult = "arrResult";
var keyJsonArrResult = "arrResult";
var keyJsonObjKlasse = "klasse";
var keyJsonObjKlasseBenutzer = "klasseBenutzer";
var keyJsonObjKlasseVeranst = "klasseVeranst";
var keyJsonObjKlasseStudiengang = "klasseStudiengang";
var keyJsonObjKlasseKarteikarte = "klasseKarteikarte";

// urlHandler

var urlParamLocation = "location";
var urlParamId = "id";

var ansichtStartseite = "startseite";
var ansichtHauptseite = "hauptseite";
var ansichtProfilseite = "profilseite";
var ansichtVeranstaltungsseite = "veranstaltungsseite";
var alleAnsichten = [ansichtStartseite,ansichtHauptseite,ansichtProfilseite,ansichtVeranstaltungsseite];
