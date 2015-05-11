$(document).ready(function() {
    
    $('#kk_erstellen_bt').click(function() {
        
        popupFenster(
            $("#kk_erstellen_popup_overlay"), 
            [$('#kk_erstellen_popup_close'),$("#kk_erstellen_cancel")],
            function() {
            	var editor = $('#vn_erstellen_beschr_input').ckeditorGet();
            	editor.destroy();

            	var dialog = $("#vn_erstellen_popup");
            	dialog.find("#vn_erstellen_titel_input").val("");
//            	dialog.find("#vn_erstellen_auswahl_semester").val(jsonBenutzer[paramSemester]);
            	selectedStudiengaenge = {};
            	$("#vn_erstellen_stg_list").empty();
            	
            	dialog.find("#vn_erstellen_beschr_input").val("");
            	dialog.find("#vn_erstellen_pass_input").val("");
            	dialog.find("#vn_erstellen_komm_erlaubt").prop("checked", true);
            	dialog.find("#vn_erstellen_bew_erlaubt").prop("checked", true);

            	dialog.find("#vn_erstellen_keiner_bearb").prop("checked", true);

            	selectedModList = {};
            	$("#vn_erstellen_mod_list").empty();

            },
            $("#vn_erstellen_ok"),
            function() {
                var dialog = $("#vn_erstellen_popup");
                var titel = dialog.find("#vn_erstellen_titel_input").val(),
                    semester = dialog.find("#vn_erstellen_auswahl_semester").val(),
                    beschr = dialog.find("#vn_erstellen_beschr_input").val(),
                    moderatorenKkBearbeiten = dialog.find("input[name=vn_bearbeitenMode_radiogb]:checked").val() != "Nur ich",
                    passw = dialog.find("#vn_erstellen_pass_input").val(),
                    kommentareErlaubt = dialog.find("#vn_erstellen_komm_erlaubt").is(':checked'),
                    bewertungenErlaubt = dialog.find("#vn_erstellen_bew_erlaubt").is(':checked');
                
                var moderatorenIDs = [];
                
                for( var key in selectedModList)
                {
                    moderatorenIDs.push(selectedModList[key][paramId]);
                }
                
                // Fehlerprüfung
                if(titel == "" || beschr == "" || $.isEmptyObject(selectedStudiengaenge))
                {
                    showError("Bitte geben Sie mindestens einen Titel, eine Beschreibung und einen Studiengang an!");
                    return false;
                }
            
                var params = {};
                params[paramTitel] = escape(titel);
                params[paramSemester] = escape(semester);
                params[paramStudiengang] = "";
                
                for(var i in selectedStudiengaenge)
            	{
                	params[paramStudiengang] += selectedStudiengaenge[i][paramStudiengang] + ",";
            	}
                	
                params[paramBeschr] = escape(beschr);
                params[paramModeratorKkBearbeiten] = moderatorenKkBearbeiten;
                params[paramKommentareErlauben] = kommentareErlaubt;
                params[paramBewertungenErlauben] = bewertungenErlaubt;
                params[paramPasswort] = escape(passw);
                params[paramModeratoren] = moderatorenIDs;
                
                var ajax = ajaxCall(veranstaltungServlet,
                        actionErstelleVeranst,
                        function(response) {
                            showInfo("Veranstaltung \""+ titel +"\"wurde erfolgreich erzeugt.");
                            fillVeranstaltungsliste();  
                        },
                        params
                    );
                return true;
            },
            $("#vn_erstellen_titel_input"),
            $("#vn_erstellen_weiter"),
            $("#vn_erstellen_zurueck")
        );
        
        $("#vn_erstellen_beschr_input").ckeditor();
        
    });
    
});

