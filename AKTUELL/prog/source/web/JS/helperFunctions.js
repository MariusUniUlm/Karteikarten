/**
 * Enthält nützliche Hilfsfunktionen, die uns die Arbeit erleichtern.
 */

/**
 * Füllt eine gegebenee selection mit optionen
 * @param select
 * @param optArray Array mit den Einträgen als String
 * @param slectedOptName Option die gewählt sein soll
 * @param clearFirst Wenn true, dann werden alle alten Optionen erst entfernt
 */
function fillSelectWithOptions(select, optArray, selectedOptName, clearFirst) 
{
	if(clearFirst == true)
		$(select).empty();

    for(var i in optArray) 
    {
        $(select).append("<option value = '" + optArray[i] +"'>"+optArray[i]+"</option>");
    }

    $(select).find("option[value='"+ selectedOptName +"']").prop('selected', true);
}

/**
 * Triggert einen Sind-Sicher-Dialog auf der GUI.
 * @param anchorElem ist das Triggerelement (jQuery- oder DOM-Element).
 * @param message ist die Nachricht, die der Benutzer bestaetigen soll.
 * @param doCriticalThing ist eine Funktion, die nach Bestaetigung mit Ok ausgefuehrt wird.
 */
function sindSieSicher(anchorElem, message, doCriticalThing, locV, locH)
{
    $("#dialog_sicher").removeClass("hidden");
    $("#dialog_sicher").css({
        top: 0,
        left: 0
    });
    
    var pos = anchorElem.offset();
    $("#dialog_sicher").offset({
        top: pos.top,
        left: pos.left
    });
    
    var clone = $("#dialog_sicher").clone();
    clone.css("visibility","hidden");
    $('body').append(clone);
    var width = clone.outerWidth();
    clone.remove();
    
    var overflow = pos.left + width - $(window).width();
    if(overflow > 0)
    {
        $("#dialog_sicher").offset({
            top: $("#dialog_sicher").offset().top,
            left: $("#dialog_sicher").offset().left - overflow
        });
    }
    
    $("#dialog_sicher").fadeIn(300);
    $("#dialog_sicher_popup_overlay").fadeIn(300);
    $("#dialog_sicher_popup_overlay").click(function() {
        $("#dialog_sicher").addClass("hidden");
        $("#dialog_sicher").fadeOut(300);
        $("#dialog_sicher_popup_overlay").fadeOut(300);
        $("#dialog_sicher_popup_overlay").off();
    });
    
    $(".dialog_sicher_frage").text(message);
    $(".dialog_sicher_ja").click(function() {
        doCriticalThing();
        $("#dialog_sicher").addClass("hidden");
        $("#dialog_sicher").fadeOut(300);
        $("#dialog_sicher_popup_overlay").fadeOut(300);
        $("#dialog_sicher_popup_overlay").off();
    });
}

/**
 * Blendet ein Popup auf der GUI ein.
 * @param popupOverlayWrapper jQuery Objekt. Element, das den dunklen Hintergrund darstellt.
 * @param closeElems Array aus jQuery Objekten. Elemente, die bei Klick das Popup schliessen.
 * @param focusElem jQuery Objekt. Element, das nach dem Oeffnen fokussiert werden soll.
 * @param submitFunc Funktion, die bei Bestaetigung ausgefuehrt wird.
 * @param cancelFunc Funktion, die bei Abbruch ausgefuehrt wird.
 * @param closeFunc Funktion, die bei Schliessen (egal auf welchem Weg!) ausgefuehrt wird.
 */
function popupFenster(popupOverlayWrapper, closeElems, closeFunc, focusElem)
{
    popupOverlayWrapper.fadeIn(300);
    popupOverlayWrapper.find(".popup_fenster").removeClass("hidden");
    focusElem.focus();
    for(var i in closeElems)
    {
        closeElems[i].click(function() {
            popupOverlayWrapper.fadeOut(300);
            popupOverlayWrapper.find(".popup_fenster").addClass("hidden");
            closeFunc();
        });
    }
    
}

/**
 * Bequeme Funktion um einen Ajax Call an ein Servlet zu senden.
 * Antwortet das Servlet mit dem Code "noerror" dann wird eine Funktion ausgefuehrt,
 * die dieser Funktion uebergeben wird.
 * Andernfalls wird standardmaessig der Default-Error-Text auf der GUI angezeigt,
 * aber man kann auch eine errorHandlingFunc uebergeben, die eine Spezialbehandlung erlaubt.
 * Zusaetzlich koennen Funktionen uebergeben werden, die bei beforeSend und complete ausgefuehrt werden,
 * um etwa eine Lade-Meldung auf der GUI anzuzeigen.
 * @param servletUrl ist ein String, der das richtige Servlet adressiert.
 * @param action ist ein String, der als Kommando fuer den Server fungiert.
 * @param params ist ein Objekt mit Parameternamen und jeweiligem Wert, die vom Server ausgelesen werden.
 * @param noerrorFunc ist eine Funktion, die bei einer Antwort mit errCode == 'noerror' ausgefuehrt wird.
 * @param errorHandlingFunc ist eine Funktion, die bei einer Antwort mit errCode != 'noerror' ausgefuehrt wird (kann optional uebergeben werden).
 * Achtung: Die errorHandlingFunc muss true zurueckgeben, falls der Error behandelt werden konnte und false andernfalls 
 * (dann wird der Default-Error-Text fuer den jeweiligen Code auf der GUI angezeigt).
 * @param beforeFunc ist eine Funktion, die beforeSend ausgefuehrt wird (kann optional uebergeben werden).
 * @param completeFunc ist eine Funktion, die bei complete ausgefuehrt wird (kann optional uebergeben werden).
 * @returns Ajax Objekt, das Informatioen ueber den Antwortstatus enthaelt.
 */
function ajaxCall(servletUrl, action, noerrorFunc, params, errorHandlingFunc, beforeFunc, completeFunc)
{
    return $.ajax({
        url: servletUrl,
        data: "action="+action+"&"+toUrlParamString(params),
        beforeSend: beforeFunc,
        success: function(jsonResponse) {
            if(verifyResponse(jsonResponse,errorHandlingFunc)) {
            	if(noerrorFunc!= undefined)
            		noerrorFunc(jsonResponse);
            }
        },
        complete: completeFunc
    });
}

/**
 * Gibt einen URL Parameter String der Form
 * key1=val1&key2=val2&key3=val3...
 * zurueck.
 * Im Unterschied zu buildUrlQuery(paramObj)
 * steht hier kein ? vorne.
 * Wird undefined uebergeben kommt ein leerer String zurueck!
 * @param paramObj enthaelt die schoen verpackten Parameter.
 */
function toUrlParamString(paramObj) 
{
    if(paramObj == undefined)
    {
        return "";
    }
    var locationSearchTmp = "";
    
    for(var param in paramObj)
    {
    	if($.isArray(paramObj[param]))
    	{
    		for( var i in paramObj[param])
    		{
                locationSearchTmp += param + "=" + paramObj[param][i] + "&";
    		}
    	}
    	else
    	{
            locationSearchTmp += param + "=" + paramObj[param] + "&";
    	}
    }
    return locationSearchTmp;
}


function addItemToList(itemMap, container, displayName, data, removeFkt, clickFkt) 
{
	if(itemMap[displayName])
		return false;
	
	var html = "<span class='itemListItem'>"; 
	
	if(clickFkt != undefined)
		html += "<a class='itemListItemName'>" + displayName + "</a>";
	else
		html += "<span class='itemListItemName'>" + displayName + "</span>";
	
	html += "<a class='octicon octicon-x itemListItemClose'></a>" +
				"</span>"
	
	// Zu jquery konvertieren
	html = $(html);
	container.append(html);
	
	// Map hinzufügen
	itemMap[displayName] = data;
	
	html.find(".itemListItemName").click(function() 
	{
		if(clickFkt != undefined)
			clickFkt(data,displayName);
	});

	html.find(".itemListItemClose").click(function() 
	{
		html.remove();

		if(removeFkt != undefined)
			removeFkt(itemMap[displayName]);

		// Aus map löschen
		delete itemMap[displayName];
	});
	
	
	
	return true;
}

/**
 * Fügt Autovervollstaendigung zu einem input[type="text"] Element hinzu.
 * Tippt der Benutzer wird ein Timer gestartet. Der bisher eingegebene String wird an den Server gesendet.
 * Dieser startet in der Datenbank eine Funktion, welche nach aehnlichen Strings sucht (Editierdistanz) und diese zurueckgibt.
 * Der Server verpackt die Daten in geeignete Objekte und schickt sie als JSON zurueck.
 * Der Client zeigt die Ergebnisse auf der GUI an. Dazu wird ein div Container mit Suchergebnissen unter dem Textfeld ausgeklappt.
 * Der Benutzer kann mit den Pfeiltasten darin navigieren und Ergebnisse entweder per Enter-Taste oder Mausklick auswaehlen.
 * <p><strong>WICHTIG:</strong> Die Objekte categories und categoryClassMapping muessen gleich gross sein!</p>
 * <p><strong>WICHTIG:</strong> Autocomplete Textfelder muessen auf required gesetzt sein!</p>
 * @param textInput jQuery Objekt des input[type="text"] Elementes
 * @param categories Object, das als Keys Strings enthaelt, welche als Unterteilungen in der Suchergebnisliste erscheinen;
 * und als Values Funktionen, welche bei Auswahl eines Suchergebnisses aus der jeweiligen Kategorie ausgefuehrt werden.
 * @param categoryClassMapping Object, das als Keys die Klassen von Objekten enthaelt, die vom Server als Suchergebnisse kommen (z.B. Benutzer, Veranstaltungen...);
 * Value ist hier jeweils die Kategorie, in die das Objekt eingeordnet werden soll.
 */
function autoComplete(textInput, categories, categoryClassMapping)
{
    var suchergHtmlStr = "<div id='suche_ergebnisse_"+textInput.attr("id")+"' class='suche_ergebnisse'>"
                   +         "<div class='sucherg_x'><span class='octicon octicon-x'></span></div>";
    for(var i in categoryClassMapping)
    {
        suchergHtmlStr +=    "<div class='sucherg_titel'>"+categoryClassMapping[i]+"</div>"
                   +         "<div id='sucherg_"+categoryClassMapping[i]+"' class='sucherg'></div>";
    }
    suchergHtmlStr +=    "</div>";
    
    var suchergJQueryObj = $(suchergHtmlStr);

    textInput.after(suchergJQueryObj);
        
    textInput.keydown(function(event) {
        if(event.keyCode == 40 || // Pfeil runter
           event.keyCode == 38 || // Pfeil hoch
           event.keyCode == 13 || // ENTER
           event.keyCode == 27)   // ESC
        {
            // Sende bei diesen Eingaben keinen Ajax Call
            // sondern navigiere in den Suchergebnissen
            handlePfeiltastenEvents(event.keyCode, suchergJQueryObj);
            event.preventDefault();
            return;
        }
        if(textInput.val() == "")
        {
            for(var i in categoryClassMapping)
            {
                $("#sucherg_"+categoryClassMapping[i]).slideUp("fast").empty();
            }
        }
        if($("#suche_ergebnisse_"+textInput.attr("id")).is(":hidden"))
        {
            $("#suche_ergebnisse_"+textInput.attr("id")).show();
        }
        suchTimer.reset();
        suchTimer.set(textInput, categories, categoryClassMapping);
    });
           
    // Reagiere auf Klick auf das x zum Schliessen des Suchergebniscontainers
    suchergJQueryObj.find(".sucherg_x span").click(function() {
        textInput.val("");
    });
}

var suchErgIterator = -1;
var autoCompleteTimerMillis = 400;

/**
 * Der Suchtimer kontrolliert, in welchen Zeitabstaenden Ajax Calls an den Server gesendet werden,
 * waehrend der Benutzer in das Textfeld tippt.
 */
var suchTimer = function(){
    var that = this,
    time = 15,
    timer;
    that.set = function(textInput, categories, categoryClassMapping) {
        timer = setTimeout(function() {
            for(var i in categoryClassMapping)
            {
                $("#sucherg_"+categoryClassMapping[i]).slideUp("fast").empty();
            }
            var params = {};
            params[paramSuchmuster] = textInput.val();
            ajaxCall (
                suchfeldServlet,
                actionSucheBenVeranst,
                function(response) {
                    var arrSuchErgebnisse = response[keyJsonArrResult];
                    fillSuchergebnisse(arrSuchErgebnisse, categories, categoryClassMapping);
                    suchErgIterator = -1;
                    for(var i in categoryClassMapping)
                    {
                        $("#sucherg_"+categoryClassMapping[i]).slideDown("fast");
                    }
                },
                params
            );
        },autoCompleteTimerMillis);
    }
    that.reset = function(){
        clearTimeout(timer);
    }
    return that;
}();

/**
 * Diese Funktion wird von autoComplete verwendet. Sie baut HTML Strings zusammen und fuegt sie in die Suchergebnisliste ein.
 * <strong>WICHTIG:</strong> Hier muss entschieden werden, welche Teile des empfangenen JSON Objekts angezeigt werden
 * (z.B. bei Benutzern: Vorname Nachname, bei Veranstaltungen: Titel). Standardmaessig werden IDs angezeigt.
 * @param arrSuchErgebnisse
 * @param categories
 * @param categoryClassMapping
 */
function fillSuchergebnisse(arrSuchErgebnisse, categories, categoryClassMapping)
{
    var isCategoryEmpty = {};
    for(var i in categoryClassMapping)
    {
        isCategoryEmpty[categoryClassMapping[i]] = true;
    }
    // Callback Function to call inside the for loop
    // @see http://stackoverflow.com/questions/4091765/assign-click-handlers-in-for-loop
    function clickHandler(categories, category, jsonSuchErgebnis)
    {
        return function() {
            // Fuehre die Funktion aus, die Suchergebnissen in dieser Kategorie zugeordnet wurde
            categories[category](jsonSuchErgebnis);
            // TODO Suchergebnisse verbergen
        }
    }
    for(var i in arrSuchErgebnisse)
    {
        var jsonSuchErgebnis = arrSuchErgebnisse[i];
        var klasse = jsonSuchErgebnis[keyJsonObjKlasse];
        var category = categoryClassMapping[klasse];
        var id = jsonSuchErgebnis[paramId];
        var suchErgHtmlString = "<div id='sucherg_"+category+"_"+id+"' class='sucherg_item'>";
        if(klasse == keyJsonObjKlasseBenutzer)
        {
            suchErgHtmlString += "<span class='octicon octicon-person'></span>" +
                                 jsonSuchErgebnis[paramVorname] + " " + jsonSuchErgebnis[paramNachname];
        }
        else if(klasse == keyJsonObjKlasseVeranst)
        {
            suchErgHtmlString += "<span class='octicon octicon-podium'></span>" +
                                 jsonSuchErgebnis[paramTitel];
        }
        // TODO Falls noch andere Objekte als Suchergebnisse kommen koennen, muessen diese hier nachgetragen werden.
        else
        {
            suchErgHtmlString += jsonSuchErgebnis[paramId];
        }
        suchErgHtmlString +=    "</div>";
        var suchErgJQueryObj = $(suchErgHtmlString);
        $("#sucherg_"+category).append(suchErgJQueryObj);
        isCategoryEmpty[category] = false;

        suchErgJQueryObj.click( clickHandler(categories, category, jsonSuchErgebnis) );
        
    }
    
    for(var i in isCategoryEmpty)
    {
        if(isCategoryEmpty[i])
        {
            $("#sucherg_"+i).append("<div class='sucherg_leer'>Nichts gefunden.</div>");
        }
    }
}

/**
 * Diese Funktion wird von autoComplete genutzt.
 * Sie ermoeglicht es mit der Tastatur in den Suchergebnissen zu navigieren.
 * @param pressedKey Keycode
 * @param suchergJQueryObj Container mit Suchergebnissen
 */
function handlePfeiltastenEvents(pressedKey, suchergJQueryObj) {
    var arr = suchergJQueryObj.find(".sucherg_item");
    if(pressedKey == 40) // Pfeil runter
    {
        $(arr[suchErgIterator]).css({"background":"none", "color":"#4a4a4a"});
        if(suchErgIterator+1 < arr.length)
            suchErgIterator++;
    }
    else if(pressedKey == 38) // Pfeil hoch
    {
        $(arr[suchErgIterator]).css({"background":"none", "color":"#4a4a4a"});
        if(suchErgIterator-1 >= 0)
            suchErgIterator--;
    }
    else if(pressedKey == 13) // ENTER
    {
        $(arr[suchErgIterator]).trigger("click");
    }
    else if(pressedKey == 27) // ESC
    {
        suchergJQueryObj.find(".sucherg_x").trigger("click");
    }
    $(arr[suchErgIterator]).css({"background":"#4a4a4a", "color":"white"});
}
