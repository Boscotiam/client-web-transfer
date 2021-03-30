$(document)
    .ready(
    function(e) {

        $("#loader").fadeOut("1000");
        $(".loader").fadeOut("1000");

        if (profilUser == profil_admin){
            $('#menuPartner').addClass('active');
        }else{
            $('#menuGuichet').addClass('active');
        }

        var partner;
        var partnerId;
        var partnerName;

        var action;
        var valueAction;
        var codeGuichet;
        var libelle;

        var url = window.location.search;
        var id = url.substring(url.lastIndexOf("=")+1);
        var sp = id.split('_');
        partner = sp[0];
        partnerId = sp[1];

        getSession();

        function getSession(){
          $("#loader").fadeIn("1000");
          appRoutes.controllers.Enquiries.getPartnerData().ajax({
              success: function (json) {
                  if (json.code == 200) {
                      getGuichets();
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

        function getGuichets(varpartner){
            $("#loader").fadeIn("1000");
            $('#tbody').html('');
            $('#titlePage').html('');
            if (partner == null || partner == '') {partner = partnerInSession;}
            varpartner = partner;
            appRoutes.controllers.GuichetManager.getGuichets(partner).ajax({
                success: function (json) {
                    if (json.code == 200) {

                        if (profilUser == profil_admin){
                          $('#titlePage').append(labelPartner + " " + json.name + ": " + labelPartnerUsers);
                        }

                        var guichets = json.guichets;
                        var numLine = 0;
                        for (var i in guichets) {
                            var html = '';
                            numLine += 1;
                            html += '<tr id="' + guichets[i].code + '" class="tr-shadow">';
                            html += '<td>' + numLine + '</td>';
                            html += '<td class="code desc" id="' + guichets[i].code + '">' + guichets[i].code + '</td>';
                            html += '<td class="numeroCompte" id="' + guichets[i].numeroCompte + '">' + guichets[i].numeroCompte + '</td>';
                            html += '<td class="solde" id="' + guichets[i].solde + '">' + guichets[i].solde + '</td>';
                            html += '<td class="libelle" id="' + guichets[i].libelle + '">' + guichets[i].libelle + '</td>';
                            html += '<td class="adresse" id="' + guichets[i].adresse + '">' + guichets[i].adresse + '</td>';
                            html += '<td class="dateCreation" id="' + guichets[i].dateCreation + '">' + guichets[i].dateCreation + '</td>';
                            if (guichets[i].open == "YES"){
                                html += '<td class="open" id="' + guichets[i].etat + '"><span class="status--process">' + guichets[i].open + '</span></td>';
                            }
                            else{
                                html += '<td class="open" id="' + guichets[i].open + '"><span class="status--denied">' + guichets[i].open + '</span></td>';
                            }
                            html += '<td>';
                            html += '<div class="table-data-feature">';

                            if (guichets[i].etat == "ACTIF"){
                                html += '<button class="block item addMore" title="' + labelLock + '" data-toggle="modal" data-target="#modalDialog">';
                                    html += '<i class="zmdi zmdi-lock"></i>';
                                html += '</button>';
                            }else{
                                html += '<button class="deblock item addMore" title="' + labelUnlock + '" data-toggle="modal" data-target="#modalDialog">';
                                    html += '<i class="zmdi zmdi-lock-open"></i>';
                                html += '</button>';
                            }

                            if (profilUser == profil_sender || profilUser == profil_mixte){
                                html += '<button class="btnDepot item addMore" title="' + labelDepot + '" data-toggle="modal" data-target="#modalDepot">';
                                    html += '<i class="zmdi zmdi-hourglass-alt"></i>';
                                html += '</button>';
                            }

                            html += '</div>';
                            html += '</td>';

                            html += '</tr>';
                            html += '<tr class="spacer"></tr>';
                            $('#tbody').append(html);
                        }


                        $(".block").click(function(){
                          action = "verrou";
                          codeGuichet = $(this).parent().parent().parent().attr('id');
                          libelle = $(this).parent().parent().parent().children('.libelle').attr('id');
                          valueAction = "INACTIF";
                          $('#messageDialog').html('');
                          $('#messageDialog').append(labelMessageLock + ' ' +
                                                      codeGuichet + ' ' + libelle + ' ?');
                        });

                        $(".deblock").click(function(){
                          action = "verrou";
                          codeGuichet = $(this).parent().parent().parent().attr('id');
                          libelle = $(this).parent().parent().parent().children('.libelle').attr('id');
                          valueAction = "ACTIF";
                          $('#messageDialog').html('');
                          $('#messageDialog').append(labelMessageUnlock + ' ' +
                                                      codeGuichet + ' ' + libelle + ' ?');
                        });

                         $('.btnDepot').click(function (e) {
                             codeGuichet = $(this).parent().parent().parent().attr('id');
                             libelle = $(this).parent().parent().parent().children('.libelle').attr('id');
                             $('#codeGuichet').val(codeGuichet);
                             $('#libelleGuichet').val(libelle);
                         });


                    }
                    else if(json.code == 201){
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

                  $("#loader").fadeOut("1000");
                }
            });
        }

        $('#btnConfirm').click(function (e) {
            if (action == 'verrou'){
                var data = {
                    'code' : codeGuichet,
                    'valueAction' : valueAction
                };
                doVerrou(data);
            }
        });

        function doVerrou(data) {
            $(".loader").fadeIn("1000");
            $('#btnConfirm').attr("disabled", true);
            appRoutes.controllers.GuichetManager.verrouGuichet().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    $(".loader").fadeOut("1000");
                    $('#btnConfirm').attr("disabled", false);
                    $('#btnClose').click();
                    if(json.code == 200){
                        getSession();
                        doShowSuccess(json.message);
                    }else {
                        doShowWarning(json.message);
                    }
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }
                    $(".loader").fadeOut("1000");
                    $('#btnConfirm').attr("disabled", false);
                    $('#btnClose').click();
                }
            });
        }

        $('#btnAdd').click(function (e) {
            verifyBeforeAdd();
        });

        $(".guichetAdd").keydown(function( event ) {
            if(event.keyCode == 13){
                verifyBeforeAdd();
            }
        });

        function verifyBeforeAdd(){
            $('#btnAdd').attr("disabled", true);
            var libelle = $('#guichetName').val();
            var adresse = $('#guichetAdresse').val();
            if(libelle == ''){
                doShowErrorAdd(labelVerifyNom);
                $('#btnAdd').attr("disabled", false);
            }
            else if(adresse == ''){
                doShowErrorAdd(labelVerifyAdresse);
                $('#btnAdd').attr("disabled", false);
            }
            else {
                var data ={
                    'nom' : libelle,
                    'adresse' : adresse
                };
                doAddGuichet(data);
            }
        }

        function doAddGuichet(data){
            $(".loader").fadeIn("1000");
            appRoutes.controllers.GuichetManager.addGuichet().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if (json.code == 200) {
                        $('#btnCancelAdd').click();
                        cleanAllfieldsPopupAdd();
                        doShowSuccess(json.message);
                        getSession();
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
            $('#guichetName').val('');
            $('#guichetAdresse').val('');
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
                    'guichet' : codeGuichet,
                    'montant' : montant
                };
                doDepot(data);
            }
        }

        function doDepot(data){
            $(".loader").fadeIn("1000");
            appRoutes.controllers.GuichetManager.depotGuichet().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if (json.code == 200) {
                        $('#btnCancelDepot').click();
                        $('#codeGuichet').val('');
                        $('#libelleGuichet').val('');
                        $('#montantDepot').val('');
                        doShowSuccess(json.message);
                        getSession();
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


