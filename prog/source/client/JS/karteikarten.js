/**
 * @author Andreas, Marius
 */

/**
 * Baut zu einem gegebenen Karteikarten Objekt das DOM Element zusammen.
 * Dafuer wird ein im HTML File definiertes 'Grundgeruest' fuer Karteikarten verwendet.
 * Dieses wird mit Informationen aus dem gegebenen Objekt gefuellt.
 * @param karteikarteJson eine Karteikarte als JSON Objekt wie es der Server versendet
 * @returns kkDom DOM Element, das eine Karteikarte darstellt
 */
function buildKarteikarte(karteikarteJson)
{
    // Hole Daten der Karteikarte
    var kkId = karteikarteJson[paramId],
        kkTitel = karteikarteJson[paramTitel],
        kkType = karteikarteJson[paramType],
        kkVeranstaltung = karteikarteJson[paramVeranstaltung],
        kkInhalt = karteikarteJson[paramInhalt],
        kkBewertung = karteikarteJson[paramBewertung],
        kkAenderungsdatum = karteikarteJson[paramAenderungsdatum];
    
    // Hole das in HTML definierte Karteikarten Element
    var kkDom = $("#templatekarteikarte").clone(true,true);
    kkDom.removeAttr("id");
    kkDom.attr("data-kkid", kkId);
    kkDom.show();

    // Fuelle die Karteikarte mit den Daten
    fillKarteiKarte(kkDom,karteikarteJson);
    
    // Rechte pruefen und entsprechende Buttons anzeigen bzw. ausblenden
    
    // Wenn Benutzer kein Admin oder Ersteller 
    if( !checkIfAllowedVn(veranstaltungsObject, true, true, false) ||
            (checkIfAllowedVn(veranstaltungsObject, false, false, true) &&
                !veranstaltungsObject[paramModeratorKkBearbeiten]) )
    {
        kkDom.find(".KKbearbeiten").hide();
        kkDom.find(".KKloeschen").hide();
    }
    // Wenn Benutzer Moderator mit Bearbeitungsrechten: Zeige Buttons wieder an.
    if(checkIfAllowedVn(veranstaltungsObject, false, false, true) &&
            veranstaltungsObject[paramModeratorKkBearbeiten])
    {
    	// remove statt show, weil show zu display:inline führt
        kkDom.find(".KKbearbeiten").removeAttr("style");
        kkDom.find(".KKloeschen").removeAttr("style");
    }
    // Handelt es sich um die Root-Karteikarte: Verberge Bearbeiten- und Loeschen-Buttons
    if(veranstaltungsObject[paramErsteKarteikarte] == kkId)
    {
        kkDom.find(".KKbearbeiten").hide();
        kkDom.find(".KKloeschen").hide();
    }
    // Wenn Bewertungen fuer diese Veranstaltung nicht erlaubt: Verberge Bewertungsbuttons
    if(!veranstaltungsObject[paramBewertungenErlauben])
        kkDom.find(".kk_votes").hide();
    // Wenn Kommentare fuer diese Veranstaltung nicht erlaubt: Verberge Kommentar-Box
    if(!veranstaltungsObject[paramKommentareErlauben])
    {
        kkDom.find(".kk_kommheader").hide();
        kkDom.find(".kk_kommbox").hide();
        kkDom.find(".kk_notizen_head").outerWidth("100%");
    }

    // Registriere Klick Handler auf Buttons
    
    kkDom.find(".KKbearbeiten").click(function(){
    	processKK_editClick($(this));
	});
    
    kkDom.find(".KKloeschen").click(function(){
    	sindSieSicher(kkDom.find(".KKloeschen"), "Wollen Sie diese Karteikarte wirklich löschen?", function(){
    		var params = {};
    		params[paramId] = kkId;
    		ajaxCall(karteikartenServlet, actionDeleteKk, function(){    			
    			displayKarteikarte(veranstaltungsObject[paramErsteKarteikarte], undefined, true);
                $.when(vnInhaltsverzeichnis.init()).done(function(){
                    vnInhaltsverzeichnis.showEintrag(veranstaltungsObject[paramErsteKarteikarte]);
                });
    			showInfo("Karteikarte gelöscht.");
    		}, params);
    	});
    });
    
    kkDom.find(".permalink").click(function(){
        // Baue die eindeutige URL fuer eine Karteikarte zusammen
   	 	kkUrl = location.host + location.pathname + "?location=veranstaltungsseite&id="+ veranstaltungsObject[paramId] +
			"&"+paramURLKkID+"=" + 	kkId;
   	 	
    	$("#link_copy_data").html(kkUrl);
    	
    	new PopupFenster(
    	        $("#link_copy_popup_overlay"), 
    			[$("#link_copy_ok"), $("#link_copy_popup_close")], 
    			function(){ $("#link_copy_data").val("") }, 
    			$("#link_copy_ok")
    	).show();
    });
    
	// Reagiere auf das Scrollen zu dieser Karteikarte
    kkDom.waypoint({
        // Waypoints werden aktiviert, wenn alle KKs geladen wurden
	    enabled: false,
	    handler: function(direction) {
	    	vnInhaltsverzeichnis.showEintrag(kkId);
	    },
	    offset: "40px",
	    group: 'kk_waypoint_group',
	    continuous: false
	});
    
    // Ueberschrift Karteikarten sind hier schon fertig, Funktion wird verlassen
    if(kkInhalt == "" && kkType == paramKkText)
    	return kkDom;
    
    // Setup Kommentare/Notizen CSS-Tabs
    kkDom.find(".kk_tabs_radio").attr("name", "kk_tabs_"+karteikarteJson[paramId]);
    kkDom.find(".kk_tabs_radio").each(function() {
        var radioID = $(this).attr("id")+"_"+karteikarteJson[paramId];
        $(this).attr("id", radioID);
        $(this).next("label").attr("for", radioID);
    });
    
    // Notiz Editor
    kkDom.find(".kk_notizen_body").ckeditor(function() {
        // Notiz speichern Button
    	var that = this;
    	$.when(setNotiz(kkDom, kkId)).done(function(){
    		that.on('change', function() {
    			kkDom.find(".kk_notizen_speichern").addClass("changed");
    		});
    	});
    	
	}, ckEditorNotizConfig);
    
    setNotiz(kkDom, kkId);
    
    // Notiz absenden Handler
    kkDom.find(".kk_notizen_speichern").click(function(){
        var params = {};
        text = kkDom.find(".kk_notizen_body").val();
        params[paramId] = kkId;
        params[paramInhalt] = text;
        ajaxCall(
            notizServlet,
            actionSpeichereNotiz,
            function(response) {
                showInfo("Gespeichert.");
            },
            params
        );
    });
    
    // Voting
    if(karteikarteJson[paramHatGevoted] == true)
    {
    	kkDom.find(".kk_voteup").css("opacity","0.1");
    	kkDom.find(".kk_votedown").css("opacity","0.1");
    	kkDom.find(".kk_voteup").css("cursor","default");
    	kkDom.find(".kk_votedown").css("opacity","default");
    }
    else
    {
    	kkDom.find(".kk_voteup").click(function() {
			$.when(voteKkUp(karteikarteJson[paramId])).done(function()
				{
					doVoteKkGUI(kkDom, parseInt(kkDom.find(".kk_votestat").html())+1);
				});
		});
    	kkDom.find(".kk_votedown").click(function() {
			$.when(voteKkDown(karteikarteJson[paramId])).done(function()
				{
					doVoteKkGUI(kkDom, parseInt(kkDom.find(".kk_votestat").html())-1);
				});
		});
    }
    
    // Notizen einklappen
    kkDom.find(".kk_notizen_head").click(function(e){
        var radioID = $(e.target).attr("for");
        if($("#"+radioID).prop("checked"))
        {
            $("#"+radioID).prop("checked", false);
            e.preventDefault();
            e.stopPropagation();
        }
    });
    
    // Kommentare einklappen
    kkDom.find(".kk_kommheader").click(function(e){
        var radioID = $(e.target).attr("for");
        if($("#"+radioID).prop("checked"))
        {
            $("#"+radioID).prop("checked", false);
            e.preventDefault();
            e.stopPropagation();
        }
    });

    // Kommentare ckEditor
    kkDom.find(".kk_kommerstellen .antw").ckeditor(ckEditorKommentarConfig);

    // Neues Thema absenden Handler
    kkDom.find(".komm_submit_bt").click(function(){
		if(kkDom.find(".antw").ckeditorGet().getData().trim() == "")
		{
			showError("Bitte geben sie etwas in das Kommentarfeld ein.");
			return;
		}
		$.when(erstelleNeuesThemaKk(karteikarteJson[paramId], kkDom.find(".antw").ckeditorGet().getData())).done(function(){
		    kkDom.find(".antw").ckeditorGet().setData("");
			var params = {};
			params[paramId] = kkId;
			ajaxCall(
			    kommentarServlet,
			    actionLeseThemaKommentar,
			    function(response) {
			    	var arr = response[keyJsonArrResult];
			        showHauptKommentare(kkDom,arr);
			    },
			    params
			);
		});
	});
    
	kkDom.find(".kk_kommheader_refresh").click(function(){
	    // Kommentare laden und anzeigen
	    var params = {};
	    params[paramId] = kkId;
		ajaxCall(
		    kommentarServlet,
		    actionLeseThemaKommentar,
		    function(response) {
		    	arr = response[keyJsonArrResult];
		        showHauptKommentare(kkDom,arr);
		    },
		    params
		);
	});
	kkDom.find(".kk_kommheader_refresh").trigger("click");
	
    return kkDom;
}

/**
 * Traegt die Daten aus einem gegebenen Karteikarten JSON-Objekt
 * in ein gegebenes Karteikarten DOM-Element ein.
 * @param domElem
 * @param json
 */
function fillKarteiKarte(domElem, json){
	        
	// set Rating
	domElem.find(".kk_votestat").html(json[paramBewertung]);
	domElem.find(".kk_titel").text(json[paramTitel]);
	
	// Attribute setzen
	domElem.find(".kk_info_attr").empty();
	domElem.find(".kk_info_attr").append(createAttrStr(json[paramAttribute]));
	
	fillVerweise(domElem,json[paramVerweise]);
	
	// detect type and add content
	switch (json[paramType]) {
        case paramKkText:
        	domElem.find(".kk_inhalt").addClass("inhalt_text");
        	if(json[paramInhalt].trim() == "")
        	{
        	    var kkOptionen = domElem.find(".kk_optionen").clone(true,true);
        		domElem.html("<div class='kk_ueberschrift_titel'>"+json[paramTitel]+"</div>");
        		domElem.find(".kk_ueberschrift_titel").append(kkOptionen);
        	}
        	else
        	{
        		domElem.find(".inhalt_text").html(json[paramInhalt]);
        	}
        	break;
        case paramKkBild:
        	var caption;
        	var kkInhalt;
        	if(json[paramInhalt] != ""){
	        	caption = domElem.find(".kk_inhalt").clone();
	        	caption.addClass("inhalt_text");
	        	caption.html(json[paramInhalt]);
	        	kkInhalt = domElem.find(".kk_inhalt");
        	}
        	domElem.find(".kk_inhalt").addClass("inhalt_bild");
        	image = $(document.createElement("img"));
        	image.attr("src","files/images/"+json[paramId]+".png?"+CryptoJS.MD5(new Date().getTime()+""));
        	image.attr("onerror","this.src='files/general/default.png'");
        	image.css("max-width","100%");
        	domElem.find(".inhalt_bild").html(image);

        	if(json[paramInhalt] != "")
        		caption.insertAfter(kkInhalt);
        	break;
        case paramKkVideo:
        	var caption;
        	var kkInhalt;
        	if(json[paramInhalt] != ""){
        		caption = domElem.find(".kk_inhalt").clone();
	        	caption.addClass("inhalt_text");
	        	caption.html(json[paramInhalt]);
	        	kkInhalt = domElem.find(".kk_inhalt");
        	}
        	domElem.find(".kk_inhalt").addClass("inhalt_video");
        	video = $(document.createElement("video"));
        	video.attr("autobuffer","");
        	video.attr("controls","");
        	video.append("<source src='files/videos/"+json[paramId]+".mp4' type='video/mp4'></source>");
        	video.append("Your browser does not support the video tag.");
        	domElem.find(".inhalt_video").html(video);

        	if(json[paramInhalt] != "")
        		caption.insertAfter(kkInhalt);
	}

    // Klone die Buttonbar mit Votes und Optionen neben den Titel fuer Smartphoneansicht
    var kkButtonbar = domElem.find(".kk_buttonbar").clone(true,true);
    domElem.find(".kk_titel").append(kkButtonbar);
}

/**
 * Wandelt das vom Server empfangene Boolean Array, 
 * das die Verweise zu einer Karteikarte repraesentiert
 * in einen fuer die GUI nutzbaren HTML String um.
 * Verweise werden als span's mit der Klasse 'kk_attr_chip' dargestellt.
 * @param arrAttr
 * @returns HTML String
 */
function createAttrStr(arrAttr){
	strArr = [];
	if(arrAttr[0] == true)
		strArr.push("<span class='kk_attr_chip'>Satz</span>");
	if(arrAttr[1] == true)
		strArr.push("<span class='kk_attr_chip'>Lemma</span>");
	if(arrAttr[2] == true)
		strArr.push("<span class='kk_attr_chip'>Beweis</span>");
	if(arrAttr[3] == true)
		strArr.push("<span class='kk_attr_chip'>Definition</span>");
	if(arrAttr[4] == true)
		strArr.push("<span class='kk_attr_chip'>Wichtig</span>");
	if(arrAttr[5] == true)
		strArr.push("<span class='kk_attr_chip'>Grundlage</span>");
	if(arrAttr[6] == true)
		strArr.push("<span class='kk_attr_chip'>Zusatzinfo</span>");
	if(arrAttr[7] == true)
		strArr.push("<span class='kk_attr_chip'>Exkurs</span>");
	if(arrAttr[8] == true)
		strArr.push("<span class='kk_attr_chip'>Beispiel</span>");
	if(arrAttr[9] == true)
		strArr.push("<span class='kk_attr_chip'>Übung</span>");
	
	if(strArr.length == 0)
		strArr.push("<span class='kk_info_keine_attr'>Keine Attribute angegeben.</span>");
	
	return concatStrArr(strArr, "");
}

/**
 * Baut die Verweise einer Karteikarte in das DOM Element ein.
 * @param domKk Karteikarte DOM Element
 * @param verweisArr Array aus {paramType, paramId, paramTitel}
 */
function fillVerweise(domKk, verweisArr)
{
	rel_vor = [];
	rel_zusatz = [];
	rel_sonstiges = [];
	rel_uebung = [];
	
	for(i in verweisArr)
	{
		txt = "<a onclick='displayKarteikarte("+verweisArr[i][paramId]+", null, false);'>" + verweisArr[i][paramTitel] +"</a>";

		if(verweisArr[i][paramType] == "V_VORAUSSETZUNG")
			rel_vor.push(txt);
		else if(verweisArr[i][paramType] == "V_UEBUNG")
			rel_uebung.push(txt);
		else if(verweisArr[i][paramType] == "V_ZUSATZINFO")
			rel_zusatz.push(txt);
		else if(verweisArr[i][paramType] == "V_SONSTIGES")
			rel_sonstiges.push(txt);
	}
	
	if(rel_vor.length == 0)
		rel_vor.push("<span class='kk_info_keine_verw'>Keine Verweise angegeben.</span>");
	if(rel_zusatz.length == 0)
		rel_zusatz.push("<span class='kk_info_keine_verw'>Keine Verweise angegeben.</span>");
	if(rel_sonstiges.length == 0)
		rel_sonstiges.push("<span class='kk_info_keine_verw'>Keine Verweise angegeben.</span>");
	if(rel_uebung.length == 0)
		rel_uebung.push("<span class='kk_info_keine_verw'>Keine Verweise angegeben.</span>");
	

	domKk.find(".kk_rel_vorraus").html(concatStrArr(rel_vor, ", "));
	domKk.find(".kk_rel_zusatz").html(concatStrArr(rel_zusatz, ", "));
	domKk.find(".kk_rel_sonstiges").html(concatStrArr(rel_sonstiges, ", "));
	domKk.find(".kk_rel_uebung").html(concatStrArr(rel_uebung, ", "));
	
}

/**
 * Laedt ein Karteikarten JSON Objekt mit gegebener ID vom Server.
 * @param id
 * @returns Ajax Objekt
 */
function getKarteikarteByID(id){
	var params = {};
    params[paramKkId] = id;
    params[paramVnId] = veranstaltungsObject[paramId];
    karteikarteJSON = {};
    return ajaxCall(karteikartenServlet, actionGetKarteikarteByID, 
        function(response) {
			karteikarteJSON = response;
		},
        params
    );
}

/**
 * Laed eine eventuell vorhandene Notiz des aktuell eingeloggten Benutzers 
 * zu einer Karteikarte vom Server und traegt diese in das DOM Element ein.
 * @param kkDom
 * @param kkId
 * @returns Ajax Objekt
 */
function setNotiz(kkDom, kkId)
{
	var params = {};
	text = kkDom.find(".kk_notizen_body").val();
    params[paramId] = kkId;
	return ajaxCall(
	    notizServlet,
	    actionLeseNotiz,
	    function(response) {
	    	kkDom.find(".kk_notizen_body").val(response[paramInhalt]);
	    	if(response[paramInhalt].trim() != "")
	    		kkDom.find(".kk_notiz_gesetzt_wrapper").html(
	    		        "<span class='kk_notiz_gesetzt'></span>");
	    	else
	    	    kkDom.find(".kk_notiz_gesetzt_wrapper").hide();
	    },
	    params
	);
}

/**
 * Aktualisiert die Bewertungsbuttons nach einer Bewertung.
 * Neue Bewertung wird gesetzt, Pfeile werden ausgegraut und Klick Handler entfernt.
 * @param domKomm
 * @param vote (Number) Bewertung
 */
function doVoteKkGUI(domKomm, vote){
	domKomm.find(".kk_voteup").css("opacity","0.1");
	domKomm.find(".kk_votedown").css("opacity","0.1");
	domKomm.find(".kk_voteup").css("cursor","default");
	domKomm.find(".kk_votedown").css("cursor","default");
	domKomm.find(".kk_voteup").off("click");
	domKomm.find(".kk_votedown").off("click");
	domKomm.find(".kk_votestat").html(vote);
}

/**
 * Sendet eine positive Bewertung zu einer gegebenenen Karteikarten ID an den Server.
 * @param kommId 
 * @returns Ajax Objekt
 */
function voteKkUp(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(karteikartenServlet, actionVoteKarteikarteUp, 
	        function(response) {},
	        params
	    );
}

/**
 * Sendet eine negative Bewertung zu einer gegebenenen Karteikarten ID an den Server.
 * @param kommId
 * @returns Ajax Objekt
 */
function voteKkDown(kommId)
{
	var params = {};
    params[paramId] = kommId;
	return ajaxCall(karteikartenServlet, actionVoteKarteikarteDown, 
	        function(response) {},
	        params
	    );
}