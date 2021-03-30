$(document)
    .ready(
    function(e) {

        $("#loader").fadeOut("1000");
        $(".loader").fadeOut("1000");
        //$(".imloadAdd").fadeOut("1000");
        $('#menuDepotGuichet').addClass('active');

        $('.datepicker').datepicker({
          format: 'yyyy-mm-dd'
        });

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

        function getTransactions(begin, end, page){
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

            appRoutes.controllers.Enquiries.getDepotGuichets(begin, end, page).ajax({
                success: function (json) {
                    if (json.code == 200) {

                        var transactions = json.depots;
                        //var numLine = 0;
                        var numLine = (json.per_page * json.current_page) - json.per_page;
                        if (json.current_page == -1){numLine = 0}
                        for (var i in transactions) {
                           var html = '';
                           numLine += 1;
                           html += '<tr id="' + transactions[i].reference + '">';
                           html += '<td>' + numLine + '</td>';
                           html += '<td class="reference" id="' + transactions[i].reference + '">' + transactions[i].reference + '</td>';
                           html += '<td class="guichet" id="' + transactions[i].guichet + '">' + transactions[i].guichet + '</td>';
                           html += '<td class="codeGuichet" id="' + transactions[i].codeGuichet + '">' + transactions[i].codeGuichet + '</td>';
                           html += '<td class="compte" id="' + transactions[i].compte + '">' + transactions[i].compte + '</td>';
                           html += '<td class="text-right montant" id="' + transactions[i].montant + '">' + transactions[i].montant + '</td>';
                           html += '<td class="date" id="' + transactions[i].date + '">' + transactions[i].date + '</td>';
                           html += '<td class="etat" id="' + transactions[i].etat + '">' + transactions[i].etat + '</td>';
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
                               getTransactions(begin, end, page);
                           }
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

                    $(".#loader").fadeOut("1000");
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


    });
