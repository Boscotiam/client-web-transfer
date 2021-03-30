$(document)
    .ready(
    function(e) {

        $("#loader").fadeOut("1000");
        $(".loader").fadeOut("1000");
        $('#menuTransaction').addClass('active');

        $('.datepicker').datepicker({
          format: 'yyyy-mm-dd'
        });

        var url = window.location.search;
        var partnerInURL = url.substring(url.lastIndexOf("=")+1);

        getSession();

        function getSession(){
            $("#loader").fadeIn("1000");
            appRoutes.controllers.Enquiries.getPartnerData().ajax({
                success: function (json) {
                    if (json.code == 200) {
                        getTransactions();
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

        function getTransactions(begin, end, partner, page){
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

            if (partnerInURL != null || partnerInURL != '') partner = partnerInURL;

            appRoutes.controllers.Enquiries.getTransactions(begin, end, partner, page).ajax({
                success: function (json) {
                    if (json.code == 200) {

                        var transactions = json.transactions;
                        //var numLine = 0;
                        var numLine = (json.per_page * json.current_page) - json.per_page;
                        if (json.current_page == -1){numLine = 0}

                        for (var i in transactions) {
                            var html = '';
                            numLine += 1;
                            html += '<tr id="' + transactions[i].transaction + '" class="tr-shadow">';
                            html += '<td>' + numLine + '</td>';
                            html += '<td class="transaction desc" id="' + transactions[i].transaction + '">' + transactions[i].transaction + '</td>';
                            html += '<td class="transmetter" id="' + transactions[i].transmetter + '">' + transactions[i].transmetter + '</td>';
                            html += '<td class="montant text-right" id="' + transactions[i].montant + '">' + transactions[i].montant + '</td>';
                            html += '<td class="date" id="' + transactions[i].date + '">' + transactions[i].date + '</td>';
                            html += '<td class="etat" id="' + transactions[i].etat + '"><span class="block-email">' + transactions[i].etat + '</span></td>';
                            html += '<td class="payer" id="' + transactions[i].payer + '">' + transactions[i].payer + '</td>';
                            html += '<td class="payReference" id="' + transactions[i].payReference + '">' + transactions[i].payReference + '</td>';
                            html += '<td class="datePay" id="' + transactions[i].datePay + '">' + transactions[i].datePay + '</td>';
                            html += '<td>';
                            html += '<div class="table-data-feature">';

                            html += '<button class="btn-detail item addMore" title="' + btnDetail + '" data-toggle="modal" data-target="#modalTransaction">';
                                html += '<i class="zmdi zmdi-more"></i>';
                            html += '</button>';

                            if (profilUser == profil_sender || profilUser == profil_mixte){
                                html += '<button class="btnUpdate item addMore" title="' + labelUpdate + '" data-toggle="modal" data-target="#modalUpdate">';
                                    html += '<i class="zmdi zmdi-edit"></i>';
                                html += '</button>';
                            }

                            html += '</div>';
                            html += '</td>';

                            html += '<input type="hidden" class="sender" id="' + transactions[i].sender + '">';
                            html += '<input type="hidden" class="transmetterTel" id="' + transactions[i].transmetterTel + '">';
                            html += '<input type="hidden" class="beneficiaire" id="' + transactions[i].beneficiaire + '">';
                            html += '<input type="hidden" class="beneficiaireTel" id="' + transactions[i].beneficiaireTel + '">';

                            html += '</tr>';
                            html += '<tr class="spacer"></tr>';
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
                               getTransactions(begin, end, partner, page);
                           }
                       });

                       $(".btn-detail").click(function(){
                            var transaction = $(this).parent().parent().parent().attr('id');
                            var data = {
                                  'codePayment' : transaction
                            };
                            doGetTransactionTransfer(data);
                       });

                       $('.btnUpdate').click(function (e) {
                           $('#codeTransaction').val($(this).parent().parent().parent().children('.transaction').attr('id'));
                           $('#transmetter').val($(this).parent().parent().parent().children('.transmetter').attr('id'));
                           $('#transmetterTel').val($(this).parent().parent().parent().children('.transmetterTel').attr('id'));
                           $('#beneficiaire').val($(this).parent().parent().parent().children('.beneficiaire').attr('id'));
                           $('#beneficiaireTel').val($(this).parent().parent().parent().children('.beneficiaireTel').attr('id'));
                       });


                    } else if(json.code == 201){
                        doShowWarning(json.message);
                    }else{
                        $(location).attr('href',"/");
                    }
                    $("#loader").fadeOut("1000");
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {

                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }

                    $(".loader").fadeOut("1000");
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
            }else{getTransactions();}
        }

        function doGetTransactionTransfer(data){
            $('#btnSearchPayment').attr("disabled", true);
            $(".loader").fadeIn("1000");
            $('#tbodyTransaction').html('');
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
                    }
                    else {
                        doShowErrorAdd(data.message);
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

        $('#btnUpdate').click(function (e) {
            verifyBeforeUpdate();
        });

        $(".transactionUpdate").keydown(function( event ) {
            if(event.keyCode == 13){
                verifyBeforeUpdate();
            }
        });

        function verifyBeforeUpdate(){
            $('#btnUpdate').attr("disabled", true);
            var transmetter = $('#transmetter').val();
            var transmetterTel = $('#transmetterTel').val();
            var beneficiaire = $('#beneficiaire').val();
            var beneficiaireTel = $('#beneficiaireTel').val();
            if(transmetter == ''){
                doShowErrorUpdate(labelVerifyTransmetter);
                $('#btnUpdate').attr("disabled", false);
            }
            else if(transmetterTel == ''){
                doShowErrorUpdate(labelVerifyTransmetterTel);
                $('#btnUpdate').attr("disabled", false);
            }
            else if(beneficiaire == ''){
                doShowErrorUpdate(labelVerifyBeneficiaire);
                $('#btnUpdate').attr("disabled", false);
            }
            else if(beneficiaireTel == ''){
                doShowErrorUpdate(labelVerifyBeneficiaireTel);
                $('#btnUpdate').attr("disabled", false);
            }
            else {
                var data ={
                    'transaction' : $('#codeTransaction').val(),
                    'transmetter' : transmetter,
                    'transmetterTel' : transmetterTel,
                    'beneficiaire' : beneficiaire,
                    'beneficiaireTel' : beneficiaireTel
                };
                doUpdate(data);
            }
        }

        function doUpdate(data){
            $(".loader").fadeIn("1000");
            $('#btnUpdate').attr("disabled", true);
            appRoutes.controllers.UserManager.updateTransaction().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if (json.code == 200) {
                        $('#btnCancelUpdate').click();
                        doShowSuccess(json.message);
                        getSession();
                    }
                    else{
                        doShowErrorUpdate(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnUpdate').attr("disabled", false);
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnUpdate').attr("disabled", false);
                }
            });
        }



    });
