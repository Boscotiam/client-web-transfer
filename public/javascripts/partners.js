$(document)
    .ready(
    function(e) {

        $(".loader").fadeOut("1000");
        //$(".imloadAdd").fadeOut("1000");
        $('#menuPartner').addClass('active');

        var partner;
        var partnerId;
        var partnerName;
        var telephone;
        var email;

        getSession();

        function getSession(){
          $(".loader").fadeIn("1000");
          appRoutes.controllers.Enquiries.getPartnerData().ajax({
              success: function (json) {
                  if (json.code == 200) {
                      getPartners();
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

        function getPartners(name, page){
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
          name = $('#name').val();

          appRoutes.controllers.PartnerManager.getPartners(name, page).ajax({
              success: function (json) {
                  if (json.code == 200) {


                      var countries = json.countries;
                      $('#partnerCountry').html('');
                      //$('#partnerCountryUpdate').html('');
                      var htmlEnteteProfil = '<option value="">' + labelCountry + '</option>';
                      $('#partnerCountry').append(htmlEnteteProfil);
                      //$('#partnerCountryUpdate').append(htmlEnteteProfil);
                      for (var i in countries) {
                          var html = '<option value="' + countries[i].code + '">' + countries[i].libelle + '</option>';
                          $('#partnerCountry').append(html);
                          //$('#partnerCountryUpdate').append(html);
                      }

                      var banks = json.banks;
                      $('#partnerBank').html('');
                      //$('#partnerBankUpdate').html('');
                      var htmlEnteteProfil = '<option value="">' + labelBank + '</option>';
                      $('#partnerBank').append(htmlEnteteProfil);
                      //$('#partnerBankUpdate').append(htmlEnteteProfil);
                      for (var i in banks) {
                          var html = '<option value="' + banks[i].code + '">' + banks[i].libelle + '</option>';
                          $('#partnerBank').append(html);
                          //$('partnerBankUpdate').append(html);
                      }

                      var partners = json.partners;
                      //var numLine = 0;
                      var numLine = (json.per_page * json.current_page) - json.per_page;
                      if (json.current_page == -1){numLine = 0}
                      for (var i in partners) {
                         var html = '';
                         numLine += 1;
                         html += '<tr id="' + partners[i].consumerId + '" class="tr-shadow">';
                         html += '<td>' + numLine + '</td>';
                         html += '<td class="name" id="' + partners[i].name + '"><span class="block-email">' + partners[i].name + '</span></td>';
                         html += '<td class="type desc" id="' + partners[i].type + '">' + partners[i].type + '</td>';
                         html += '<td class="adress" id="' + partners[i].adress + '">' + partners[i].adress + '</td>';
                         html += '<td class="telephone" id="' + partners[i].telephone + '">' + partners[i].telephone + '</td>';
                         html += '<td class="email" id="' + partners[i].email + '">' + partners[i].email + '</td>';
                         html += '<td>';
                         html += '<div class="table-data-feature">';
                              html += '<button class="btnDetail item addMore" title="' + labelDetail + '" data-toggle="modal" data-target="#modalDetail">';
                                  html += '<i class="zmdi zmdi-more"></i>';
                              html += '</button>';
                              /*html += '<button class="btnUpdate item addMore"  title="'+ labelUpdate + '" data-toggle="modal" data-target="#modalUpdate">';
                                  html += '<i class="zmdi zmdi-edit"></i>';
                              html += '</button>';*/
                              if (partners[i].type == 'SENDER'){
                                  html += '<button class="btnDepot item addMore" title="' + labelDepot + '" data-toggle="modal" data-target="#modalDepot">';
                                      html += '<i class="zmdi zmdi-hourglass-alt"></i>';
                                  html += '</button>';
                              }
                              html += '<button class="btnTransactions item addMore"  title="'+ labelTransactions + '">';
                                  html += '<i class="zmdi zmdi-format-list-numbered"></i>';
                              html += '</button>';
                              html += '<button class="btnUsers item addMore"  title="'+ labelUsers + '">';
                                  html += '<i class="zmdi zmdi-accounts"></i>';
                              html += '</button>';
                         html += '</div>';
                         html += '</td>';

                         html += '<input type="hidden" class="id" id="' + partners[i].id + '">';
                         html += '<input type="hidden" class="country" id="' + partners[i].country + '">';
                         html += '<input type="hidden" class="accountPRINC" id="' + partners[i].accountPRINC + '">';
                         html += '<input type="hidden" class="accountCOMM" id="' + partners[i].accountCOMM + '">';
                         html += '<input type="hidden" class="accountCHARGE" id="' + partners[i].accountCHARGE + '">';
                         html += '<input type="hidden" class="balancePRINC" id="' + partners[i].balancePRINC + '">';
                         html += '<input type="hidden" class="balanceCOMM" id="' + partners[i].balanceCOMM + '">';
                         html += '<input type="hidden" class="balanceCHARGE" id="' + partners[i].balanceCHARGE + '">';
                         html += '<input type="hidden" class="codeBank" id="' + partners[i].codeBank + '">';
                         html += '<input type="hidden" class="codeGuichet" id="' + partners[i].codeGuichet + '">';
                         html += '<input type="hidden" class="accountBank" id="' + partners[i].accountBank + '">';
                         html += '<input type="hidden" class="rib" id="' + partners[i].codeGuichet + '">';

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
                             getPartners(name, page);
                         }
                     });


                     $('.btnDetail').click(function (e) {
                         partner = $(this).parent().parent().parent().attr('id');
                         $('#partnerNameDetail').html($(this).parent().parent().parent().children('.name').attr('id'));
                         $('#partnerTypeDetail').html($(this).parent().parent().parent().children('.type').attr('id'));
                         $('#partnerAdressDetail').html($(this).parent().parent().parent().children('.adress').attr('id'));
                         $('#partnerTelDetail').html($(this).parent().parent().parent().children('.telephone').attr('id'));
                         $('#partnerEmailDetail').html($(this).parent().parent().parent().children('.email').attr('id'));
                         $('#partnerCountryDetail').html($(this).parent().parent().parent().children('.country').attr('id'));
                         $('#partnerAccountPrincDetail').html($(this).parent().parent().parent().children('.accountPRINC').attr('id'));
                         $('#partnerAccountCommDetail').html($(this).parent().parent().parent().children('.accountCOMM').attr('id'));
                         $('#partnerAccountChargeDetail').html($(this).parent().parent().parent().children('.accountCHARGE').attr('id'));
                         $('#partnerBalancePrincDetail').html(formatMillier($(this).parent().parent().parent().children('.balancePRINC').attr('id')));
                         $('#partnerBalanceCommDetail').html(formatMillier($(this).parent().parent().parent().children('.balanceCOMM').attr('id')));
                         $('#partnerBalanceChargeDetail').html(formatMillier($(this).parent().parent().parent().children('.balanceCHARGE').attr('id')));
                         var bank = $(this).parent().parent().parent().children('.codeBank').attr('id') + $(this).parent().parent().parent().children('.codeGuichet').attr('id') +
                                    $(this).parent().parent().parent().children('.accountBank').attr('id') + $(this).parent().parent().parent().children('.rib').attr('id');
                         $('#partnerBankDataDetail').html(bank);
                     });

                     $('.btnDepot').click(function (e) {
                          partner = $(this).parent().parent().parent().attr('id');
                          telephone = $(this).parent().parent().parent().children('.telephone').attr('id');
                          email = $(this).parent().parent().parent().children('.email').attr('id');
                          $('#partnertNameDepot').val($(this).parent().parent().parent().children('.name').attr('id'));
                      });

                     $(".btnUsers").click(function(){
                         partner = $(this).parent().parent().parent().attr('id');
                         partnerId = $(this).parent().parent().parent().children('.id').attr('id');
                         $(location).attr('href', "/partners/users?partner=" + partner + "_" + partnerId);
                     });

                     $(".btnTransactions").click(function(){
                         partner = $(this).parent().parent().parent().attr('id');
                         partnerId = $(this).parent().parent().parent().children('.id').attr('id');
                         $(location).attr('href', "/transactions?partner=" + partner);
                     });


                  } else if(json.code == 201){
                      doShowWarning(json.message);
                      var countries = json.countries;
                      $('#partnerCountry').html('');
                      //$('#partnerCountryUpdate').html('');
                      var htmlEnteteProfil = '<option value="">' + labelCountry + '</option>';
                      $('#partnerCountry').append(htmlEnteteProfil);
                      //$('#partnerCountryUpdate').append(htmlEnteteProfil);
                      for (var i in countries) {
                          var html = '<option value="' + countries[i].code + '">' + countries[i].libelle + '</option>';
                          $('#partnerCountry').append(html);
                          //$('#partnerCountryUpdate').append(html);
                      }

                      var banks = json.banks;
                      $('#partnerBank').html('');
                      //$('#partnerBankUpdate').html('');
                      var htmlEnteteProfil = '<option value="">' + labelBank + '</option>';
                      $('#partnerBank').append(htmlEnteteProfil);
                      //$('#partnerBankUpdate').append(htmlEnteteProfil);
                      for (var i in banks) {
                          var html = '<option value="' + banks[i].code + '">' + banks[i].libelle + '</option>';
                          $('#partnerBank').append(html);
                          //$('partnerBankUpdate').append(html);
                    }
                  }
                  else{
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
          var name = $('#nameSearch').val();
          if (name == ''){
              doShowError(labelVerifySearch);
          }else{getPartners();}
        }

        $('#myTabs a').click(function (e) {

            var selectedTab = $(this).attr('id');
            //console.log("selectedTab: " + selectedTab);

            var tabedBank = true;
            var continu = true;

            if(selectedTab == 'aAddDataPartner'){
                $(this).tab('show');
            }
            else if(selectedTab == 'aAddDataBankPartner'){
                $('.form-part').each(function() { //loop through each form-part
                    if ($(this).val() == ''){
                        $('#aAddDataBankPartner').removeAttr('data-toggle');
                        doShowErrorAdd(labelVerifyPartner);
                        tabedBank = false;
                        continu = false;
                        return false;
                    }
                });
                if(tabedBank == true && $('#partnerType').val() == 'PAYER'){
                    $('#aAddDataBankPartner').attr('data-toggle', 'tab');
                    $(this).tab('show');
                }
            }


        });

        $('#btnSaveDepot').click(function (e) {
            verifyBeforeAdd();
        });

        $(".partnerAdd").keydown(function( event ) {
            if(event.keyCode == 13){
                verifyBeforeAdd();
            }
        });

        function verifyBeforeAdd(){
            $('#btnAdd').attr("disabled", true);
            var name = $('#partnertName').val();
            var type = $('#partnerType').val();
            var adress = $('#partnerAdress').val();
            var telephone = $('#partnerTel').val();
            var email = $('#partnerEmail').val();
            var country = $('#partnerCountry').val();

            var bank = $('#partnerBank').val();
            var guichet = $('#partnerGuichet').val();
            var account = $('#partnerBankAccount').val();
            var rib = $('#partnerRIB').val();
            if(name == ''){
                doShowErrorAdd(labelVerifyName);
                $('#btnAdd').attr("disabled", false);
            }
            else if(type == ''){
                doShowErrorAdd(labelVerifyType);
                $('#btnAdd').attr("disabled", false);
            }
            else if(adress == ''){
                doShowErrorAdd(labelVerifyAdress);
                $('#btnAdd').attr("disabled", false);
            }
            else if(telephone == ''){
                doShowErrorAdd(labelVerifyTel);
                $('#btnAdd').attr("disabled", false);
            }
            else if(email == ''){
                doShowErrorAdd(labelVerifyEmail);
                $('#btnAdd').attr("disabled", false);
            }
            else if(country == ''){
                doShowErrorAdd(labelVerifyCountry);
                $('#btnAdd').attr("disabled", false);
            }
            else {
                var data ={
                    'name' : name,
                    'type' : type,
                    'adress' : adress,
                    'telephone' : telephone,
                    'email' : email,
                    'country' : country,
                    'bank' : bank,
                    'guichet' : guichet,
                    'account' : account,
                    'rib' : rib
                };
                doAddPartner(data);
            }
        }

        function doAddPartner(data){
            $(".loader").fadeIn("1000");
            appRoutes.controllers.PartnerManager.addPartner().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if (json.code == 200) {
                        $('#btnCancelAdd').click();
                        cleanAllfieldsPopupAdd();
                        doShowSuccess(json.message);
                        getPartners();
                    }
                    else{
                        doShowErrorAdd(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnAdd').attr("disabled", false);
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnAdd').attr("disabled", false);
                }
            });
        }

        function cleanAllfieldsPopupAdd(){
            $('#partnertName').val('');
            $('#partnerType').val('');
            $('#partnerAdress').val('');
            $('#partnerTel').val('');
            $('#partnerEmail').val('');
            $('#partnerCountry').val('');
            $('#partnerBank').val('');
            $('#partnerGuichet').val('');
            $('#partnerBankAccount').val('');
            $('#partnerRIB').val('');
        }

        $('#btnSaveDepot').click(function (e) {
            verifyBeforeDepot();
        });

        $("#montantDepot").keydown(function( event ) {
            if(event.keyCode == 13){
                verifyBeforeDepot();
            }
        });

        function verifyBeforeDepot(){
            $('#btnSaveDepot').attr("disabled", true);
            var montant = $('#montantDepot').val();
            if(montant == ''){
                doShowErrorDepot(labelVerifyAmount);
                $('#btnSaveDepot').attr("disabled", false);
            }
            else {
                var data ={
                    'partner' : partner,
                    'telephone' : telephone,
                    'email' : email,
                    'montant' : montant
                };
                doDepot(data);
            }
        }

        function doDepot(data){
            $(".loader").fadeIn("1000");
            appRoutes.controllers.PartnerManager.depotSender().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if (json.code == 200) {
                        $('#btnCancelDepot').click();
                        $('#montantDepot').val('');
                        $('#partnertNameDepot').val('');
                        doShowSuccess(json.message);
                        getPartners();
                    }
                    else{
                        doShowErrorDepot(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnSaveDepot').attr("disabled", false);
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnSaveDepot').attr("disabled", false);
                }
            });
        }

    });


