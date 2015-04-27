/**
 * @author mk
 */

var currentProfilID;

// Statische Handler einmal registrieren
$(document).ready(function() {
    
	$("#profil_loeschen_bt").click(function() {
		sindSieSicher($("#profil_loeschen_bt"), "Wollen sie das Profil wirklich löschen?",function(){
		    var params = {};
		    params[paramId] = currentProfilID;
			ajaxCall(
			    profilServlet,
			    actionDeleteBenutzer,
			    function() {
			        showInfo("Profil wurde erfolgreich gelöscht!");
			        // Hat sich der Benutzer selbst geloescht oder war das
			        // ein Admin, der jmd anders geloescht hat.
			        if(currentProfilID == jsonBenutzer[paramId])
			        {
			            jsonBenutzer = undefined;
	                    gotoStartseite();
			        }
			        else
			        {
			            gotoHauptseite();
			        }
			    },
			    params
			);
		}, "bottom","left");
	});

    // Reagiere auf die Auswahl einer Datei
    $('#profil_avatar_aendern_file').change(function() {
        var filenameFull = $('#profil_avatar_aendern_file').val();
        var fileName = filenameFull.split(/(\\|\/)/g).pop();
        $("#profil_avatar_aendern_file_name").html(fileName);
        $("#profil_avatar_aendern_file_name").css("color","yellow");
        if(fileName != "")
            $("#profil_avatar_submit").fadeIn();
        else
            $("#profil_avatar_submit").fadeOut();
    });
    
    getProfilStudiengaenge();
    registerProfilSpeichernEvents();
    registerAvatarAendernEvent();
});
/**
 * Zeigt die Daten des Benutzers im Profil an
 * jsonBenutzer enthält immer das aktuelle BenutzerObjekt.
 */
function fillProfilseite() {
    
    currentProfilID = getUrlParameterByName(urlParamId);
    
    if(currentProfilID == jsonBenutzer[paramId])
    {
    	fillMyProfil(jsonBenutzer[paramNutzerstatus] == "ADMIN");
    }
    else
    {
        // Benutzer zeigt anderes Profil an
    	var params = {};
    	params[paramId] = currentProfilID;
        return ajaxCall(profilServlet,
            			actionGetOtherBenutzer,
            function(response)
            {
        		fillOtherProfil(response,jsonBenutzer[paramNutzerstatus] == "ADMIN");
            },
            params,
            function(erroTxt)
            {
                // Angefragter Benutzer existiert evntl nicht
                gotoHauptseite();
                
                return false;
            }
        );
    }
}

/**
 * Holt die Studiengaenge und traegt sie in das select Element ein.
 */
function getProfilStudiengaenge() {
    return ajaxCall(startseitenServlet,
    				actionGetStudiengaenge,
    				function(response) 
    				{
				    	$("#profil_studiengang_input").empty();
				    	var studgArr = response[keyJsonArrResult];
				    	for(var i in studgArr) {
				    		$("#profil_studiengang_input").append("<option>"+studgArr[i]+"</option>");
				    	}
			    	}
		    	);
}

function fillMyProfil(isAdmin)
{
    $("#profil_email_input").attr("disabled",false);
    $("#profil_vorname_input").attr("disabled",false);
    $("#profil_nachname_input").attr("disabled",false);
    
	$("#profil_avatar_aendern_file_name").show();
	$(".profil_avatar_overlay").show();
	$(".profil_loeschen").show();
	$("#profil_einstellungen").show();
	$("#profil_passwort ").show();
	
	$(".profil_benutzername").text(jsonBenutzer[paramVorname] +" "+jsonBenutzer[paramNachname]);
	$(".profil_avatar_img").attr("src", jsonBenutzer[paramProfilBild]);
	
    $(".profil_loeschen").show();

    $("#profil_email_input").val(jsonBenutzer[paramEmail]);
    $("#profil_vorname_input").val(jsonBenutzer[paramVorname]);
    $("#profil_nachname_input").val(jsonBenutzer[paramNachname]);
    $("#profil_matnr_input").val(jsonBenutzer[paramMatrikelNr]);
    $("#profil_rolle_input").val(jsonBenutzer[paramNutzerstatus]);
	$("#profil_studiengang_input").val(jsonBenutzer[paramStudiengang]);
    
    switch(jsonBenutzer[paramNotifyKommentare])
    {
	    case paramNotifyKommentareValVeranst:
	    	$("#notifyKommentare_input_teilgenommen").prop("checked",true);
	    	break;
	    case paramNotifyKommentareValDiskussion:
	    	$("#notifyKommentare_input_diskussionen").prop("checked",true);
	    	break;
	    case paramNotifyKommentareValKeine:
	    	$("#notifyKommentare_input_nie").prop("checked",true);
    }
    
    $("#notifyVeranstAenderung_input").prop("checked",jsonBenutzer[paramNofityVeranstAenderung]);
    $("#notifyKarteikartenAenderung_input").prop("checked",jsonBenutzer[paramNotifyKarteikartenAenderung]);
	
	if(!isAdmin)
	{
		$("#profil_matnr_input").prop("disabled", true);
		$("#profil_studiengang_input").prop("disabled", true);
		$("#profil_rolle_input").prop("disabled", true);
	}
	else
	{
		$("#profil_matnr_input").prop("disabled", false);
		$("#profil_studiengang_input").prop("disabled", false);
		$("#profil_rolle_input").prop("disabled", false);
	}
}

function fillOtherProfil(benutzer, isAdmin)
{
	$(".profil_benutzername").text(benutzer[paramVorname] +" "+benutzer[paramNachname]);
	$(".profil_avatar_img").attr("src", benutzer[paramProfilBild]);

    $("#profil_email_input").val(benutzer[paramEmail]);
    $("#profil_vorname_input").val(benutzer[paramVorname]);
    $("#profil_nachname_input").val(benutzer[paramNachname]);
    $("#profil_matnr_input").val(benutzer[paramMatrikelNr]);
    $("#profil_rolle_input").val(benutzer[paramNutzerstatus]);
	$("#profil_studiengang_input").val(benutzer[paramStudiengang]);
    
    switch(benutzer[paramNotifyKommentare])
    {
	    case paramNotifyKommentareValVeranst:
	    	$("#notifyKommentare_input_teilgenommen").prop("checked",true);
	    	break;
	    case paramNotifyKommentareValDiskussion:
	    	$("#notifyKommentare_input_diskussionen").prop("checked",true);
	    	break;
	    case paramNotifyKommentareValKeine:
	    	$("#notifyKommentare_input_nie").prop("checked",true);
    }
    
    $("#notifyVeranstAenderung_input").prop("checked",benutzer[paramNofityVeranstAenderung]);
    $("#notifyKarteikartenAenderung_input").prop("checked",benutzer[paramNotifyKarteikartenAenderung]);
	
	if(!isAdmin)
	{
		$("#profil_avatar_aendern_file_name").hide();
		$(".profil_avatar_overlay").hide();
		$(".profil_loeschen").hide();
		$("#profil_einstellungen").hide();
		$("#profil_passwort ").hide();

		$("#profil_email_input").prop("disabled", true);
		$("#profil_vorname_input").prop("disabled", true);
		$("#profil_nachname_input").prop("disabled", true);
		$("#profil_matnr_input").prop("disabled", true);
		$("#profil_studiengang_input").prop("disabled", true);
		$("#profil_rolle_input").prop("disabled", true);
	}
	else
	{
		$("#profil_avatar_aendern_file_name").show();
		$(".profil_avatar_overlay").show();
		$(".profil_loeschen").show();
		$("#profil_einstellungen").show();
		$("#profil_passwort ").show();

		$("#profil_email_input").prop("disabled", false);
		$("#profil_vorname_input").prop("disabled", false);
		$("#profil_nachname_input").prop("disabled", false);
		$("#profil_matnr_input").prop("disabled", false);
		$("#profil_studiengang_input").prop("disabled", false);
		$("#profil_rolle_input").prop("disabled", false);
	}
}

/**
 * Liest die Daten aus den Feldern aus und uebertraegt Sie zum Server.
 */
function registerProfilSpeichernEvents() {
    
    $("#profil_daten_form").submit(function(event) 
    {
        // Felder auslesen
        var email = $("#profil_email_input").val();
        var vorname = $("#profil_vorname_input").val();
        var nachname = $("#profil_nachname_input").val();
        var notifyVeranst = $("#notifyVeranstAenderung_input").prop("checked");
        var notifyKarteikarten = $("#notifyKarteikartenAenderung_input").prop("checked");
        var notifyKommentare =  $("input[name=profil_notifyKommentareRb]:checked").val();
        if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
        {
            var matnr = $("#profil_matnr_input").val();
            var studiengang = $("#profil_studiengang_input").val();
            var rolle = $("#profil_rolle_input").val();
        }
        var params = {};
        params[paramId] = getUrlParameterByName(urlParamId);
        params[paramVorname] = vorname;
        params[paramNachname] = nachname;
        params[paramNofityVeranstAenderung] = notifyVeranst;
        params[paramNotifyKarteikartenAenderung] = notifyKarteikarten;
        params[paramNotifyKommentare] = notifyKommentare;
        params[paramEmailNew] = email;
        if(jsonBenutzer[paramNutzerstatus] == "ADMIN")
        {
            params[paramNutzerstatus] = rolle;
            params[paramStudiengang] = studiengang;
            params[paramMatrikelNr] = matnr;
        }
        ajaxCall(
            profilServlet,
            actionAendereProfil,
            function(response) {
                showInfo("Änderungen gespeichert.");
                $.when(getBenutzer()).done(function() {
                    fillProfilseite();
                    fillMyPersonalBox();
                });
            },
            params,
            undefined,
            function() {
                $("#profil_daten_speichern").val("Lädt...");
                $("#profil_daten_speichern").prop("disabled", true);
            },
            function() {
                $("#profil_daten_speichern").val("Speichern");
                $("#profil_daten_speichern").prop('disabled', false);
            }
        );
        // Verhindert das normale Absenden des Formulars
        event.preventDefault();         
    });
            
    $("#profil_passwort_form").submit(function(event) 
    {
        // Felder auslesen
        var pwNeu = $("#profil_passwort_input").val();
        var pwNeuWdh = $("#profil_passwort_wdh_input").val();
        var pwAlt = $("#profil_passwort_alt_input").val();
        // Fehleingaben abfangen
        if(pwNeu != pwNeuWdh)
        {
            // Wdh-Feld leeren
            $("#profil_passwort_wdh_input").val("");
            $("#profil_passwort_wdh_input").focus();
            $("#profil_passwort_wdh_input").css("border","4px solid IndianRed");
            showError("Bitte prüfen Sie Ihre Eingaben.");
        }
        else
        {
        	pwNeu = CryptoJS.MD5(pwNeu);
        	pwAlt = CryptoJS.MD5(pwAlt);
        	
            var params = {};
            params[paramPasswortNew] = escape(pwNeu);
            params[paramPasswort] = escape(pwAlt);
            params[paramId] = escape(currentProfilID);
            ajaxCall(
                profilServlet,
                actionAenderePasswort,
                function(response) {
                    showInfo("Änderungen gespeichert.");
                    fillProfilseite();
                },
                params,
                function(errCode) {
                    if(errCode == "loginfailed") 
                    {
                        $("#profil_passwort_alt_input").css("border","4px solid IndianRed");
                        $("#profil_passwort_alt_input").val("");
                        $("#profil_passwort_alt_input").focus();
                        showError("Bitte prüfen Sie Ihre Eingaben.");
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                },
                function() {
                    $("#profil_passwort_speichern").val("Lädt...");
                    $("#profil_passwort_speichern").prop('disabled', true);
                },
                function() {
                    $("#profil_passwort_speichern").val("Speichern");
                    $("#profil_passwort_speichern").prop('disabled', false);
                }
            );
        }
        $("#profil_passwort_input").val("");
        $("#profil_passwort_wdh_input").val("");
        $("#profil_passwort_alt_input").val("");
        // Verhindert das normale Absenden des Formulars
        event.preventDefault();
    });
}

/**
 * Laedt ein neues Profilbild hoch.
 */
function registerAvatarAendernEvent() {
    $("#profil_avatar_aendern_form").submit(function(event) {
        event.preventDefault();
    	var file = $('#profil_avatar_aendern_file')[0].files[0];
    	var filenameFull = $('#profil_avatar_aendern_file').val();
    	var fileName = filenameFull.split(/(\\|\/)/g).pop();
    	if(fileName.toLowerCase().indexOf(".jpg") < 0 && 
    			fileName.toLowerCase().indexOf(".jpeg") < 0 &&
    			fileName.toLowerCase().indexOf(".bmp") < 0 &&
    			fileName.toLowerCase().indexOf(".png") < 0)
    	{
    		showError("Leider werden nur die Formate jpg/jpeg, bmp oder png unterstützt.");
    	}
    	else
    	{
    		uploadFile(file, function(response) {
    			

            	var errFkt = function(errCode) 
            	{
            		if(errCode == "invalidparam") 
                    {
                        showError("Bitte geben Sie eine gültige Datei an.");
                        return true;
                    }
            		return false;
				};
				
    			if(verifyResponse(response,errFkt))
    			{
            		showInfo("Änerungen gespeichert.");
            		$.when(getBenutzer()).done(function() {
                 		fillProfilseite();
                 		fillMyPersonalBox();
					});
    			}
    		},
    		actionUploadProfilBild,
    		function() {
    		    // beforeSend
    		    $("#profil_avatar_submit").val("Lädt...");
    		    $("#profil_avatar_submit").prop("disabled", true);
    		},
    		function() {
    		    // complete
    		    $("#profil_avatar_submit").val("Avatar ändern");
                $("#profil_avatar_submit").prop("disabled", false);
                $("#profil_avatar_submit").hide(0);
    		});
    	}
        event.preventDefault();
    });
}
