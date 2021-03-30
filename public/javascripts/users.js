$(document)
    .ready(
    function(e) {

        $("#loader").fadeOut("1000");
        $(".loader").fadeOut("1000");

        if (profilUser == profil_admin){
            $('#menuPartner').addClass('active');
        }else{
            $('#menuUser').addClass('active');
        }

        var partner;
        var partnerId;
        var partnerName;

        var action;
        var valueAction;
        var idUser;
        var prenom;
        var nom;
        var telephone;
        var email;

        var url = window.location.search;
        var id = url.substring(url.lastIndexOf("=")+1);
        var sp = id.split('_');
        partnerId = sp[1];

        getSession();

        function getSession(){
          $("#loader").fadeIn("1000");
          appRoutes.controllers.Enquiries.getPartnerData().ajax({
              success: function (json) {
                  if (json.code == 200) {
                      getUsers();
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

        function getUsers(varpartner){
            $("#loader").fadeIn("1000");
            $('#tbody').html('');
            $('#titlePage').html('');
            if (partnerId == null || partnerId == '') {partnerId = idPartnerInSession;}
            varpartner = partnerId;
            appRoutes.controllers.UserManager.getUsers(varpartner).ajax({
                success: function (json) {
                    if (json.code == 200) {

                        if (profilUser == profil_admin){
                          $('#titlePage').append(labelPartner + " " + json.name + ": " + labelPartnerUsers);
                        }

                        /*var listProfil = json.profils;
                        $('#userProfil').html('');
                        $('#userProfilUpdate').html('');
                        var htmlEnteteProfil = '<option value="">' + labelProfil + '</option>';
                        $('#userProfil').append(htmlEnteteProfil);
                        $('#userProfilUpdate').append(htmlEnteteProfil);
                        for (var i in listProfil) {
                           var html = '<option value="' + listProfil[i].idProfil + '">' + listProfil[i].libelleProfil + '</option>';
                           $('#userProfil').append(html);
                           $('#userProfilUpdate').append(html);
                        }*/

                        var users = json.users;
                        var numLine = 0;
                        for (var i in users) {
                         var html = '';
                         numLine += 1;
                         html += '<tr id="' + users[i].idUser + '" class="tr-shadow">';
                         html += '<td>' + numLine + '</td>';
                         html += '<td class="nom desc" id="' + users[i].nom + '">' + users[i].nom + '</td>';
                         html += '<td class="prenom desc" id="' + users[i].prenom + '">' + users[i].prenom + '</td>';
                         html += '<td class="telephone" id="' + users[i].telephone + '">' + users[i].telephone + '</td>';
                         html += '<td class="email" id="' + users[i].email + '">' + users[i].email + '</td>';
                         html += '<td class="libelleProfil" id="' + users[i].libelleProfil + '">' + users[i].libelleProfil + '</td>';
                         if (users[i].verrou == "NO"){
                            html += '<td class="verrou" id="' + users[i].verrou + '"><span class="status--process">' + users[i].verrou + '</span></td>';
                         }
                         else{
                            html += '<td class="verrou" id="' + users[i].verrou + '"><span class="status--denied">' + users[i].verrou + '</span></td>';
                         }
                         html += '<td class="dateCreation" id="' + users[i].dateCreation + '">' + users[i].dateCreation + '</td>';
                         html += '<input type="hidden" class="idUser" id="' + users[i].idUser + '">';
                         html += '<input type="hidden" class="idProfil" id="' + users[i].idProfil + '">';
                         html += '<td>';
                         html += '<div class="table-data-feature">';

                              if (profilUser != profil_admin){
                                  html += '<button class="btnUpdateUser item addMore" title="' + labelUpdate + '" data-toggle="modal" data-target="#modalUpdateUser">';
                                      html += '<i class="zmdi zmdi-edit"></i>';
                                  html += '</button>';
                              }

                              html += '<button class="reinitPass item addMore" title="' + labelReinit + '" data-toggle="modal" data-target="#modalDialog">';
                                  html += '<i class="zmdi zmdi-redo"></i>';
                              html += '</button>';

                              if (users[i].verrou == "NO"){
                                  html += '<button class="block item addMore" title="' + labelBlock + '" data-toggle="modal" data-target="#modalDialog">';
                                       html += '<i class="zmdi zmdi-lock"></i>';
                                  html += '</button>';
                              }else{
                                    html += '<button class="deblock item addMore" title="' + labelUnBlock + '" data-toggle="modal" data-target="#modalDialog">';
                                       html += '<i class="zmdi zmdi-lock-open"></i>';
                                    html += '</button>';
                              }

                         html += '</div>';
                         html += '</td>';

                         html += '</tr>';
                         html += '<tr class="spacer"></tr>';
                         $('#tbody').append(html);
                        }


                        $(".reinitPass").click(function(){
                          action = "reinit";
                          idUser = $(this).parent().parent().parent().attr('id');
                          prenom = $(this).parent().parent().parent().children('.prenom').attr('id');
                          nom = $(this).parent().parent().parent().children('.nom').attr('id');
                          telephone = $(this).parent().parent().parent().children('.telephone').attr('id');
                          email = $(this).parent().parent().parent().children('.email').attr('id');
                          $('#messageDialog').html('');
                          $('#messageDialog').append(labelMessageReinit + ' ' +
                                                      prenom + ' ' + nom + ' ?');
                        });

                        $(".block").click(function(){
                          action = "verrou";
                          idUser = $(this).parent().parent().parent().attr('id');
                          prenom = $(this).parent().parent().parent().children('.prenom').attr('id');
                          nom = $(this).parent().parent().parent().children('.nom').attr('id');
                          valueAction = "YES";
                          $('#messageDialog').html('');
                          $('#messageDialog').append(labelMessageBlock + ' ' +
                                                      prenom + ' ' + nom + ' ?');
                        });

                        $(".deblock").click(function(){
                          action = "verrou";
                          idUser = $(this).parent().parent().parent().attr('id');
                          prenom = $(this).parent().parent().parent().children('.prenom').attr('id');
                          nom = $(this).parent().parent().parent().children('.nom').attr('id');
                          valueAction = "NO";
                          $('#messageDialog').html('');
                          $('#messageDialog').append(labelMessageDeblock + ' ' +
                                                      prenom + ' ' + nom + ' ?');
                        });

                        $('.btnUpdateUser').click(function (e) {
                          idUser = $(this).parent().parent().parent().attr('id');
                          $('#userFirstNameUpdate').val($(this).parent().parent().parent().children('.prenom').attr('id'));
                          $('#userLastNameUpdate').val($(this).parent().parent().parent().children('.nom').attr('id'));
                          $('#userTelUpdate').val($(this).parent().parent().parent().children('.telephone').attr('id'));
                          $('#userEmailUpdate').val($(this).parent().parent().parent().children('.email').attr('id'));
                          //$('#userProfilUpdate').val($(this).parent().parent().parent().children('.idProfil').attr('id'));
                        });

                    }
                    else if(json.code == 201){
                      doShowWarning(json.message);
                      /*var listProfil = json.profils;
                      $('#userProfil').html('');
                      $('#userProfilUpdate').html('');
                      var htmlEnteteProfil = '<option value="">' + labelProfil + '</option>';
                      $('#userProfil').append(htmlEnteteProfil);
                      $('#userProfilUpdate').append(htmlEnteteProfil);
                      for (var i in listProfil) {
                          var html = '<option value="' + listProfil[i].idProfil + '">' + listProfil[i].libelleProfil + '</option>';
                          $('#userProfil').append(html);
                          $('#userProfilUpdate').append(html);
                      }*/
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
            if (action == 'reinit'){
                var data = {
                        'user' : idUser,
                        'prenom' : prenom,
                        'nom' : nom,
                        'telephone' : telephone,
                        'email' : email
                    };
                doReinitPassword(data);
            }
            else if (action == 'verrou'){
                var data = {
                    'idUser' : idUser,
                    'valueAction' : valueAction
                };
                doBlockOrUnblock(data);
            }
        });

        function doReinitPassword(data){
            $(".loader").fadeIn("1000");
            $('#btnConfirm').attr("disabled", true);
            appRoutes.controllers.UserManager.reinitPassword().ajax({
                 data : JSON.stringify(data),
                 contentType : 'application/json',
                 success : function (json) {
                    $('#btnClose').click();
                     if (json.code == 200) {
                         doShowSuccess(json.message);
                     }
                     else{
                         doShowError(json.message);
                     }
                     $(".loader").fadeOut("1000");
                     $('.btnConfirm').attr("disabled", false);
                 },
                 error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                     if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                     }
                    $(".loader").fadeOut("1000");
                     $('#btnConfirm').attr("disabled", false);
                 }
             });
        }

        function doBlockOrUnblock(data) {
            $(".loader").fadeIn("1000");
            $('#btnConfirm').attr("disabled", true);
            appRoutes.controllers.UserManager.lockOrUnlockUser().ajax({
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

        $(".userAdd").keydown(function( event ) {
            if(event.keyCode == 13){
                verifyBeforeAdd();
            }
        });

        function verifyBeforeAdd(){
            $('#btnAdd').attr("disabled", true);
            var prenom = $('#userFirstName').val();
            var nom = $('#userLastName').val();
            var telephone = $('#userTel').val();
            var email = $('#userEmail').val();
            var login = $('#userLogin').val();
            //var profil = $('#userProfil').val();
            if(prenom == ''){
                doShowErrorAdd(labelVerifyPrenom);
                $('#btnAdd').attr("disabled", false);
            }
            else if(nom == ''){
                doShowErrorAdd(labelVerifyNom);
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
            else if(login == ''){
                doShowErrorAdd(labelVerifyLogin);
                $('#btnAdd').attr("disabled", false);
            }
            /*else if(profil == ''){
                doShowErrorAdd(labelVerifyProfil);
                $('#btnAdd').attr("disabled", false);
            }*/
            else {
                var data ={
                    'nom' : nom,
                    'prenom' : prenom,
                    'telephone' : telephone,
                    'email' : email,
                    'login' : login,
                    //'profil' : profil,
                    'partner' : partnerId
                };
                doAddUser(data);
            }
        }

        function doAddUser(data){
            $(".loader").fadeIn("1000");
            appRoutes.controllers.UserManager.addUser().ajax({
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
            $('#userLastName').val('');
            $('#userFirstName').val('');
            $('#userTel').val('');
            $('#userEmail').val('');
            $('#userLogin').val('');
            //$('#userProfil').val('');
        }

        $('#btnUpdate').click(function (e) {
            verifyBeforeUpdate();
        });

        $(".userUpdate").keydown(function( event ) {
            if(event.keyCode == 13){
                verifyBeforeUpdate();
            }
        });

        function verifyBeforeUpdate(){
            $('#btnUpdate').attr("disabled", true);
            var prenom = $('#userFirstNameUpdate').val();
            var nom = $('#userLastNameUpdate').val();
            var telephone = $('#userTelUpdate').val();
            var email = $('#userEmailUpdate').val();
            //var profil = $('#userProfilUpdate').val();
            if(prenom == ''){
                doShowErrorUpdate(labelVerifyNom);
                $('#btnUpdate').attr("disabled", false);
            }
            else if(nom == ''){
                doShowErrorUpdate(labelVerifyPrenom);
                $('#btnUpdate').attr("disabled", false);
            }
            else if(telephone == ''){
                doShowErrorUpdate(labelVerifyTel);
                $('#btnUpdate').attr("disabled", false);
            }
            else if(email == ''){
                doShowErrorUpdate(labelVerifyEmail);
                $('#btnUpdate').attr("disabled", false);
            }
            /*else if(profil == ''){
                doShowErrorUpdate(labelVerifyProfil);
                $('#btnUpdate').attr("disabled", false);
            }*/
            else {
                var data ={
                    'user' : idUser,
                    'nom' : nom,
                    'prenom' : prenom,
                    'telephone' : telephone,
                    'email' : email/*,
                    'profil' : profil*/
                };
                doUpdateUser(data);
            }
        }

        function doUpdateUser(data){
            $(".loader").fadeIn("1000");
            $('#btnUpdate').attr("disabled", true);
            appRoutes.controllers.UserManager.updateUser().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if (json.code == 200) {
                        $('#btnCancelUpdate').click();
                        doShowSuccess(json.message);
                        getSession();
                    }
                    else{
                        //alert(json.message);
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


