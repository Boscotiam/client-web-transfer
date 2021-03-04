$(document)
    .ready(
    function(e) {

        $("#loader").fadeOut("1000");
        $(".loader").fadeOut("1000");
        $('#menuHome').addClass('active');
        $('#rowBeneficiaryIdentify').hide();

        $('.datepicker').datepicker({
          format: 'yyyy-mm-dd'
        });

        var transferResponse;
        var infosResponse;
        var feesResponse;

        getSession();

        function getSession(){
            $("#loader").fadeIn("1000");
            appRoutes.controllers.Enquiries.getPartnerData().ajax({
                success: function (json) {
                    if (json.code == 200) {
                        getOperations();
                    }else{
                        $(location).attr('href',"/");
                    }
                    $("#loader").fadeOut("1000");
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }
                    $("#loader").fadeOut("1000");
                }
            });
        }

        function getOperations(begin, end, page){
            $("#loader").fadeIn("1000");
            $('#tbody').html('');
            $('#totalTransactions').html('');
            $('#principalBalance').html('');
            $('#feesBalance').html('');
            $('#chargeBalance').html('');
            $('#pagingBottom').html('');
            $('#divPagination').empty();
            $('#divPagination').removeData("twbs-pagination");
            $('#divPagination').unbind("page");
            $( "#begin").datepicker( "option", "dateFormat", "yyyy-MM-dd" );
            $( "#end").datepicker( "option", "dateFormat", "yyyy-MM-dd" );
            begin = $('#begin').val();
            end = $('#end').val();

            appRoutes.controllers.Enquiries.getJournal(begin, end, page).ajax({
                success: function (json) {
                    if (json.code == 200) {

                        var totalTransactions = json.totalTransactions;
                        var principalBalance = json.principalBalance;
                        var feesBalance = json.feesBalance;
                        var chargeBalance = json.chargeBalance;
                        $('#totalTransactions').append(totalTransactions);
                        $('#principalBalance').append(principalBalance);
                        $('#feesBalance').append(feesBalance);
                        $('#chargeBalance').append(chargeBalance);


                        var operations = json.operations;
                        //var numLine = 0;
                        var numLine = (json.per_page * json.current_page) - json.per_page;
                        if (json.current_page == -1){numLine = 0}
                        for (var i in operations) {
                           var html = '';
                           numLine += 1;
                           html += '<tr id="' + numLine + '">';
                           html += '<td>' + numLine + '</td>';
                           html += '<td class="libelle" id="' + operations[i].libelle + '">' + operations[i].libelle + '</td>';
                           html += '<td class="reference" id="' + operations[i].reference + '">' + operations[i].reference + '</td>';
                           html += '<td class="compte" id="' + operations[i].compte + '">' + operations[i].compte + '</td>';
                           html += '<td class="text-right montant" id="' + operations[i].montant + '">' + operations[i].montant + '</td>';
                           html += '<td class="sens" id="' + operations[i].sens + '">' + operations[i].sens + '</td>';
                           html += '<td class="date" id="' + operations[i].date + '">' + operations[i].date + '</td>';
                           html += '</tr>';
                           $('#tbody').append(html);
                        }

                       var current_page = json.current_page;
                       if(current_page == -1){current_page = 1}
                       $('#pagingBottom').append(current_page + '/' + json.total_page + ' ' + labelPaging);
                       $('#divPagination').twbsPagination({
                           totalPages: json.total_page,
                           visiblePages: 5,
                           startPage: current_page,
                           onPageClick: function (event, numPage) {
                               page = numPage;
                               getOperations(begin, end, page);
                           }
                       });


                    } else if(json.code == 201){
                        var totalTransactions = json.totalTransactions;
                        var principalBalance = json.principalBalance;
                        var feesBalance = json.feesBalance;
                        var chargeBalance = json.chargeBalance;
                        $('#totalTransactions').append(totalTransactions);
                        $('#principalBalance').append(principalBalance);
                        $('#feesBalance').append(feesBalance);
                        $('#chargeBalance').append(chargeBalance);
                    }else{
                        $(location).attr('href',"/");
                    }
                    $("#loader").fadeOut("1000");
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {

                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }

                    $("#loader").fadeOut("1000");
                }
            });
        }

        $('#btnSearch').click(
            function (e) {
                verifySearch();
        });

        function verifySearch(){
            $( "#begin").datepicker( "option", "dateFormat", "yyyy-MM-dd" );
            $( "#end").datepicker( "option", "dateFormat", "yyyy-MM-dd" );
            var begin = $('#begin').val();
            var end = $('#end').val();
            if (begin == '' && end == ''){
                doShowError(labelVerifySearch);
            }else{getOperations();}
        }

        $('#btnSend').click(
            function(e) {
                doVerifySendTransfer();
                //doShowSuccessAdd("OK RIGHT NOW");
        });

        function doVerifySendTransfer(){
            var montant = $('#montant').val();
            var transmetter = $('#transmetter').val();
            var transmetterIdentify = $('#transmetterIdentify').val();
            var beneficiary = $('#beneficiary').val();
            var mobile = $('#mobile').val();
            var destination = $('#destination').val();
            if(montant == ''){
                doShowErrorAdd(labelVerifyAmount);
            }
            else if(transmetter == ''){
                doShowErrorAdd(labelVerifyTransmetter);
            }
            else if(transmetterIdentify == ''){
                doShowErrorAdd(labelVerifyTransmetterId);
            }
            else if(beneficiary == ''){
                doShowErrorAdd(labelVerifyBenef);
            }
            else if(mobile == ''){
                doShowErrorAdd(labelVerifyMobile);
            }
            else{
                var data = {
                      'montant' : montant,
                      'transmetter' : transmetter,
                      'transmetterIdentify' : transmetterIdentify,
                      'beneficiary' : beneficiary,
                      'mobile' : mobile,
                      "destination" : destination
                };
                doSendTransfer(data);
            }
        }

        function doSendTransfer(data){
            $('#btnSend').attr("disabled", true);
            $(".loader").fadeIn("1000");
            appRoutes.controllers.Operations.sendTransfer().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if(json.code == 200){
                        transferResponse = json;
                        cleanAllFiedsPopupSendTransfer();
                        $('#btnCancelSend').click();
                        doShowSuccess(json.message);
                        getSession();
                    }
                    else {
                        doShowErrorAdd(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnSend').attr("disabled", false);

                },
                 error: function (xmlHttpReques, chaineRetourne, objetExeption) {
                     if(objetExeption == "Unauthorized"){
                         $(location).attr('href',"/");
                     }
                     $(".loader").fadeOut("1000");
                     $('#btnSend').attr("disabled", false);
                 }
            });
        }

        $('#btnCheckFees').click(
            function(e) {
                doVerifyCheckfees();
        });

        function doVerifyCheckfees(){
            var montant = $('#amountFees').val();
            var destination = $('#destinationFees').val();
            if(montant == ''){
                doShowErrorAdd(labelVerifyAmount);
            }
            else{
                var data = {
                      'montant' : montant,
                      "destination" : destination
                };
                doCheckFees(data);
            }
        }

        function doCheckFees(data){
            $('#btnCheckFees').attr("disabled", true);
            $(".loader").fadeIn("1000");
            $('#tbodyFees').html('');
            appRoutes.controllers.Operations.checkFees().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if(json.code == 200){
                        feesResponse = json;
                        var recap = json.recap;
                        for (var i in recap) {
                            var html = '';
                            html += '<tr>';
                            html += '<td>' + recap[i].label + '</td>';
                            html += '<td>' + recap[i].value + '</td>';
                            html += '</tr>';
                            $('#tbodyFees').append(html);
                        }
                    }
                    else {
                        doShowErrorFees(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnCheckFees').attr("disabled", false);

                },
                 error: function (xmlHttpReques, chaineRetourne, objetExeption) {
                     if(objetExeption == "Unauthorized"){
                         $(location).attr('href',"/");
                     }
                     $(".loader").fadeOut("1000");
                     $('#btnCheckFees').attr("disabled", false);
                 }
            });
        }

        $('#btnPrinter').click(
            function(e) {
                $('#btnCancelSend').click();
        });

        function cleanAllFiedsPopupSendTransfer(){
            $('#montant').val('');
            $('#transmetter').val('');
            $('#transmetterIdentify').val('');
            $('#beneficiary').val('');
            $('#mobile').val('');
            $('#destination').val('');
        }

        function cleanAllFiedsPopupPayTransfer(){
            $('#codePayment').val('');
            $('#beneficiaryIdentify').val('');
            $('#btnSearchPayment').attr("disabled", false);
        }

        function setPrinterTransfer(data){
            $('#printSender').html(partnerSession);
            $('#printAmount').html($('#montant').val());
            $('#printTransmetter').html($('#transmetter').val());
            $('#printBenef').html($('#beneficiary').val());
            $('#printBenefPhone').html($('#mobile').val());
            $('#printCountry').html($('#destination').val());
            var recap = data.recap;
            for (var i in recap) {
                var html = '';
                html += '<tr>';
                html += '<td>' + recap[i].label + '</td>';
                html += '<td>' + recap[i].value + '</td>';
                html += '</tr>';
                $('#tbodyPrinter').append(html);
            }

            $('#printSender2').html(partnerSession);
            $('#printAmount2').html($('#montant').val());
            $('#printTransmetter2').html($('#transmetter').val());
            $('#printBenef2').html($('#beneficiary').val());
            $('#printBenefPhone2').html($('#mobile').val());
            $('#printCountry2').html($('#destination').val());
            for (var j in recap) {
                var html = '';
                html += '<tr>';
                html += '<td>' + recap[j].label + '</td>';
                html += '<td>' + recap[j].value + '</td>';
                html += '</tr>';
                $('#tbodyPrinter2').append(html);
            }

        }

        $('#btnSearchPayment').click(
            function(e) {
            var codePayment = $('#codePayment').val();
            if(codePayment == ''){
                doShowErrorPay(labelVerifyCodePayment);
            }
            else{
                var data = {
                      'codePayment' : codePayment,
                };
                doGetTransactionTransfer(data);
            }
        });

        function doGetTransactionTransfer(data){
            $('#btnSearchPayment').attr("disabled", true);
            $(".loader").fadeIn("1000");
            appRoutes.controllers.Enquiries.getInfosPayTransfer().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if(json.code == 200){
                        infosResponse = json;
                        var recap = json.recap;
                        for (var i in recap) {
                            var html = '';
                            html += '<tr>';
                            html += '<td>' + recap[i].label + '</td>';
                            html += '<td>' + recap[i].value + '</td>';
                            html += '</tr>';
                            $('#tbodyTransaction').append(html);
                        }
                        $('#codePayment').attr("disabled", true);
                        $('#rowBeneficiaryIdentify').show();
                        $('#btnCancelPay').click();
                    }
                    else {
                        doShowErrorAdd(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    //$('#btnSearchPayment').attr("disabled", false);

                },
                 error: function (xmlHttpReques, chaineRetourne, objetExeption) {
                     if(objetExeption == "Unauthorized"){
                         $(location).attr('href',"/");
                     }
                     $(".loader").fadeOut("1000");
                     $('#btnSearchPayment').attr("disabled", false);
                 }
            });
        }

        $('#btnPay').click(
            function(e) {
                doVerifyPayTransfer();
        });

        function doVerifyPayTransfer(){
            var codePayment = $('#codePayment').val();
            var beneficiaryIdentify = $('#beneficiaryIdentify').val();
            if(beneficiaryIdentify == ''){
                doShowErrorPay(labelVerifyBenefId);
            }
            else{
                var data = {
                      "codePayment" : codePayment,
                      "beneficiaryIdentify" : beneficiaryIdentify
                };
                doPayTransfer(data);
            }
        }

        function doPayTransfer(data){
            $('#btnPay').attr("disabled", true);
            $(".loader").fadeIn("1000");
            appRoutes.controllers.Operations.payTransfer().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if(json.code == 200){
                        transferResponse = json;
                        cleanAllFiedsPopupPayTransfer();
                        doShowSuccess(json.message);
                        getSession();
                    }
                    else {
                        doShowErrorPay(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnPay').attr("disabled", false);

                },
                 error: function (xmlHttpReques, chaineRetourne, objetExeption) {
                     if(objetExeption == "Unauthorized"){
                         $(location).attr('href',"/");
                     }
                     $(".loader").fadeOut("1000");
                     $('#btnPay').attr("disabled", false);
                 }
            });
        }




    });
