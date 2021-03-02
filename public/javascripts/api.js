/**
 * Created by bosco on 09/05/2019.
 */
function formatMillier( nombre){
    nombre += '';
    var sep = ' ';
    var reg = /(\d+)(\d{3})/;
    while( reg.test( nombre)) {
        nombre = nombre.replace( reg, '$1' +sep +'$2');
    }
    return nombre;
}

function inArray(element,tab){
    var count =tab.length;
    for(var i=0;i<count;i++)
    {
        if(tab[i] === element){return true;}
    }
    return false;
}


function doShowSuccess(message){
    $('#olShowSuccess').find('div#showSuccess').remove();
    var html = '<div class="alert alert-success alertWidth" id="showSuccess" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowSuccess').prepend(html);
    $('#showSuccess').html('');
    $('#showSuccess').append(message);
    $('#showSuccess').fadeIn();
    setTimeout(function () {
        $('#showSuccess').fadeOut();
    }, 5000);
}

function doShowSuccessComment(message){
    $('#olShowSuccessComment').find('div#showSuccess').remove();
    var html = '<div class="alert alert-success alertWidth" id="showSuccessComment" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowSuccessComment').prepend(html);
    $('#showSuccessComment').html('');
    $('#showSuccessComment').append(message);
    $('#showSuccessComment').fadeIn();
    setTimeout(function () {
        $('#showSuccessComment').fadeOut();
    }, 5000);
}


function doShowWarning(message){
    $('#olShowWarning').find('div#showWarning').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showWarning" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowWarning').prepend(html);
    $('#showWarning').html('');
    $('#showWarning').append(message);
    $('#showWarning').fadeIn();
    setTimeout(function () {
        $('#showWarning').fadeOut();
    }, 5000);
}

function doShowError(message){
    $('#olShowError').find('div#showError').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showError" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowError').prepend(html);
    $('#showError').html('');
    $('#showError').append(message);
    $('#showError').fadeIn();
    setTimeout(function () {
        $('#showError').fadeOut();
    }, 5000);
}

function doShowErrorComment(message){
    $('#olShowErrorComment').find('div#showError').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorComment" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorComment').prepend(html);
    $('#showErrorComment').html('');
    $('#showErrorComment').append(message);
    $('#showErrorComment').fadeIn();
    setTimeout(function () {
        $('#showErrorComment').fadeOut();
    }, 5000);
}

function doShowErrorClose(message){
    $('#olShowErrorClose').find('div#showError').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showError" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorClose').prepend(html);
    $('#showError').html('');
    $('#showError').append(message);
    $('#showError').fadeIn();
    setTimeout(function () {
        $('#showError').fadeOut();
    }, 5000);
}

// Java script for alert and warning

/*function doShowSuccess(message){
    $('#olShowSuccess').find('div#showSuccess').remove();
    var html = '<div class="alert alert-success valider alertWidth" id="showSuccess" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowSuccess').prepend(html);
    $('#showSuccess').html('');
    $('#showSuccess').append(message);
    $('#showSuccess').fadeIn();
    setTimeout(function(){ $('#showSuccess').fadeOut(); }, 5000);
}*/

function doShowSuccessAdd(message){
    $('#olShowSuccessAdd').find('div#showSuccessAdd').remove();
    var html = '<div class="alert alert-success alertWidth" id="showSuccessAdd" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowSuccessAdd').prepend(html);
    $('#showSuccessAdd').html('');
    $('#showSuccessAdd').append(message);
    $('#showSuccessAdd').fadeIn();
    setTimeout(function(){ $('#showSuccessAdd').fadeOut(); }, 30000);
}

function doShowSuccessPay(message){
    $('#olShowSuccessPay').find('div#showSuccessPay').remove();
    var html = '<div class="alert alert-success alertWidth" id="showSuccessPay" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowSuccessPay').prepend(html);
    $('#showSuccessPay').html('');
    $('#showSuccessPay').append(message);
    $('#showSuccessPay').fadeIn();
    setTimeout(function(){ $('#showSuccessPay').fadeOut(); }, 30000);
}

function doShowSuccessModifier(message){
    $('#olShowSuccessModifier').find('div#showSuccessModif').remove();
    var html = '<div class="alert alert-success alertWidth" id="showSuccessModif" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowSuccessModifier').prepend(html);
    $('#showSuccessModif').html('');
    $('#showSuccessModif').append(message);
    $('#showSuccessModif').fadeIn();
    setTimeout(function(){ $('#showSuccessModif').fadeOut(); }, 5000);
}

function doShowError(message){
    $('#olShowError').find('div#showError').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showError" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowError').prepend(html);
    $('#showError').html('');
    $('#showError').append(message);
    $('#showError').fadeIn();
    setTimeout(function(){ $('#showError').fadeOut(); }, 5000);
}

function doShowWarning(message){
    $('#olShowErrorWarning').find('div#showErrorWarning').remove();
    var html = '<div class="alert alert-warning has-warning alertWidth" id="showErrorWarning" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorWarning').prepend(html);
    $('#showErrorWarning').html('');
    $('#showErrorWarning').append(message);
    $('#showErrorWarning').fadeIn();
    setTimeout(function(){ $('#showErrorWarning').fadeOut(); }, 5000);
}

function doShowErrorAdd(message){
    $('#olShowErrorAdd').find('div#showErrorAdd').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorAdd" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorAdd').prepend(html);
    $('#showErrorAdd').html('');
    $('#showErrorAdd').append(message);
    $('#showErrorAdd').fadeIn();
    setTimeout(function(){ $('#showErrorAdd').fadeOut(); }, 5000);
}

function doShowErrorDepot(message){
    $('#olShowErrorDepot').find('div#showErrorDepot').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorDepot" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorDepot').prepend(html);
    $('#showErrorDepot').html('');
    $('#showErrorDepot').append(message);
    $('#showErrorDepot').fadeIn();
    setTimeout(function(){ $('#showErrorDepot').fadeOut(); }, 5000);
}

function doShowErrorFees(message){
    $('#olShowErrorFees').find('div#showErrorFees').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorFees" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorFees').prepend(html);
    $('#showErrorFees').html('');
    $('#showErrorFees').append(message);
    $('#showErrorFees').fadeIn();
    setTimeout(function(){ $('#showErrorFees').fadeOut(); }, 5000);
}

function doShowErrorPay(message){
    $('#olShowErrorPay').find('div#showErrorPay').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorPay" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorPay').prepend(html);
    $('#showErrorPay').html('');
    $('#showErrorPay').append(message);
    $('#showErrorPay').fadeIn();
    setTimeout(function(){ $('#showErrorPay').fadeOut(); }, 5000);
}

function doShowErrorModifier(message){
    $('#olShowErrorModifier').find('div#showErrorModif').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorModif" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorModifier').prepend(html);
    $('#showErrorModif').html('');
    $('#showErrorModif').append(message);
    $('#showErrorModif').fadeIn();
    setTimeout(function(){ $('#showErrorModif').fadeOut(); }, 5000);
}

function doShowErrorCharge(message){
    $('#olShowErrorCharge').find('div#showErrorAdd').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorCharge" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorCharge').prepend(html);
    $('#showErrorCharge').html('');
    $('#showErrorCharge').append(message);
    $('#showErrorCharge').fadeIn();
    setTimeout(function(){ $('#showErrorCharge').fadeOut(); }, 5000);
}
function doShowErrorPrefinance(message){
    $('#olShowErrorPref').find('div#showErrorAdd').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorPrefinance" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorPref').prepend(html);
    $('#showErrorPrefinance').html('');
    $('#showErrorPrefinance').append(message);
    $('#showErrorPrefinance').fadeIn();
    setTimeout(function(){ $('#showErrorPrefinance').fadeOut(); }, 5000);
}

function doShowErrorUpdate(message){
    $('#olShowErrorUpdate').find('div#showErrorUpdate').remove();
    var html = '<div class="alert alert-danger alertWidth" id="showErrorUpdate" role="alert">' +
        '<span class="fa fa-exclamation-triangle" aria-hidden="true"></span>' +
        '<span class="sr-only">Error:</span></div>';
    $('#olShowErrorUpdate').prepend(html);
    $('#showErrorUpdate').html('');
    $('#showErrorUpdate').append(message);
    $('#showErrorUpdate').fadeIn();
    setTimeout(function () {
        $('#showErrorUpdate').fadeOut();
    }, 3000);
}