/**
 * Created by bosco on 14/06/2020.
 */
$(document)
    .ready(
    function(e) {

        $(".loader").fadeOut("1000");

        $('#btn-login').click(
            function(e) {
                $('#btn-login').attr("disabled", true);
                $(".loader").fadeIn("1000");
                var login = $('#login').val();
                var password = $('#password').val();
                var data = {
                      'login' : login,
                      "password" : password
                };
                appRoutes.controllers.LoginTransfer.connectInTransfer().ajax({
                    data : JSON.stringify(data),
                    contentType : 'application/json',
                    success : function (data) {
                        if(data.code == 200){
                           authentification();
                        }
                        else {
                            $('#erreur').html('');
                            $('#erreur').fadeIn();
                            $('#erreur').append(data.message);
                        }
                        $(".loader").fadeOut("1000");
                        $('#btn-login').attr("disabled", false);

                    },
                     error: function (xmlHttpReques, chaineRetourne, objetExeption) {
                         if(objetExeption == "Unauthorized"){
                             $(location).attr('href',"/");
                         }
                         $('#btn-login').attr("disabled", false);
                         $(".loader").fadeIn("1000");
                     }
                });
            });

        $( ".connection").keydown(function( event ) {
            if(event.keyCode == 13){
                $('#btn-login').attr("disabled", true);
                $(".loader").fadeIn("1000");
                var login = $('#login').val();
                var password = $('#password').val();
                var data = {
                      'login' : login,
                      "password" : password
                };
                appRoutes.controllers.LoginTransfer.connectInTransfer().ajax({
                    data : JSON.stringify(data),
                    contentType : 'application/json',
                    success : function (data) {
                        if(data.code == 200){
                            authentification();
                        }
                        else {
                            $('#erreur').html('');
                            $('#erreur').fadeIn();
                            $('#erreur').append(data.message);
                        }
                        $(".loader").fadeOut("1000");
                        $('#btn-login').attr("disabled", false);

                    },
                     error: function (xmlHttpReques, chaineRetourne, objetExeption) {
                         if(objetExeption == "Unauthorized"){
                             $(location).attr('href',"/");
                         }
                         $('#btn-login').attr("disabled", false);
                         $(".loader").fadeIn("1000");
                     }
                });

              }
            });

        function authentification(){
            $('#btn-login').attr("disabled", true);
            $(".loader").fadeIn("1000");
            var data = {};
            appRoutes.controllers.LoginTransfer.authentication().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if(json.code == 200){
                        $(location).attr('href', "/activation");
                    }
                    else {
                        $(location).attr('href', "/");
                    }
                    $('#btn-login').attr("disabled", false);
                    $(".loader").fadeIn("1000");

                },
                 error: function (xmlHttpReques, chaineRetourne, objetExeption) {
                     if(objetExeption == "Unauthorized"){
                         $(location).attr('href',"/");
                     }
                     $('#btn-login').attr("disabled", false);
                     $(".loader").fadeIn("1000");
                 }
            });
        }



    });
