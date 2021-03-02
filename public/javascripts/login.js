/**
 * Created by bosco on 14/06/2020.
 */
$(document)
    .ready(
    function(e) {

        $(".loader").fadeOut("1000");

        var login;
        var pass;

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
                            //console.log(data.message);
                           $(location).attr('href', "/activation");
                        }
                        else {
                            $('#erreur').html('');
                            $('#erreur').fadeIn();
                            $('#erreur').append(data.message);
                        }
                        console.log(data);
                        $(".loader").fadeOut("1000");
                        $('#btn-login').attr("disabled", false);

                    },
                     error: function (data) {
                         console.log(data);
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
                            //console.log(data.message);
                            $(location).attr('href', "/activation");
                        }
                        else {
                            $('#erreur').html('');
                            $('#erreur').fadeIn();
                            $('#erreur').append(data.message);
                        }
                        console.log(data);
                        $(".loader").fadeOut("1000");
                        $('#btn-login').attr("disabled", false);

                    },
                     error: function (data) {
                         console.log(data);
                    }
                });

              }
            });



    });
