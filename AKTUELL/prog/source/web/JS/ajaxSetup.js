/**
 * @author mk
 */

/**
 * Konfiguriert alle Ajax Calls.
 * Kommt vom Server keine Antwort,
 * wird ein Fehler ausgegeben.
 */
$.ajaxSetup({
	timeout: 1000,
	error: function(xhr, status, err) { 
	    //status === 'timeout' if it took too long.
	    //handle that however you want.
	    console.log(status,err); 
	    alert("Error: " + status);
	}
});