$(document)
    .ready(
    function(e) {

        $(".loader").fadeOut("1000");
        //$(".imloadAdd").fadeOut("1000");
        $('#menuTransaction').addClass('active');

        $('.datepicker').datepicker({
          format: 'yyyy-mm-dd'
        });

        var url = window.location.search;
        var partnerInURL = url.substring(url.lastIndexOf("=")+1);

        getSession();

        function getSession(){
            $(".loader").fadeIn("1000");
            appRoutes.controllers.Enquiries.getPartnerData().ajax({
                success: function (json) {
                    if (json.code == 200) {
                        getTransactions();
                    }else{
                        $(location).attr('href',"/");
                    }
                    $(".loader").fadeOut("1000");
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }
                    $(".loader").fadeOut("1000");
                }
            });
        }

        function getTransactions(begin, end, partner, page){
            $(".imload").fadeIn("1000");
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
                           html += '<tr id="' + transactions[i].transaction + '">';
                           html += '<td>' + numLine + '</td>';
                           html += '<td class="transaction" id="' + transactions[i].transaction + '">' + transactions[i].transaction + '</td>';
                           html += '<td class="sender" id="' + transactions[i].sender + '">' + transactions[i].sender + '</td>';
                           html += '<td class="text-right montant" id="' + transactions[i].montant + '">' + transactions[i].montant + '</td>';
                           html += '<td class="date" id="' + transactions[i].date + '">' + transactions[i].date + '</td>';
                           html += '<td class="etat" id="' + transactions[i].etat + '">' + transactions[i].etat + '</td>';
                           html += '<td class="payer" id="' + transactions[i].payer + '">' + transactions[i].payer + '</td>';
                           html += '<td class="payReference" id="' + transactions[i].payReference + '">' + transactions[i].payReference + '</td>';
                           html += '<td class="datePay" id="' + transactions[i].datePay + '">' + transactions[i].datePay + '</td>';
                           html += '<td><button type="button" class=" btn btn-default btn-xs btn-detail" data-toggle="modal" data-target="#modalTransaction">' + btnDetail + '</button></td>';
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
                               getTransactions(begin, end, partner, page);
                           }
                       });

                       $(".btn-detail").click(function(){
                            var transaction = $(this).parent().parent().children('.transaction').attr('id');
                            var data = {
                                  'codePayment' : transaction
                            };
                            doGetTransactionTransfer(data);
                       });


                    } else if(json.code == 201){
                        doShowWarning(json.message);
                    }else{
                        $(location).attr('href',"/");
                    }
                    $(".imload").fadeOut("1000");
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {

                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }

                    $(".imload").fadeOut("1000");
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
                    console.log(json);
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



    });
