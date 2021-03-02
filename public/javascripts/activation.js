/**
 * Created by bosco on 14/06/2020.
 */
$(document)
    .ready(
    function(e) {

        $(".loader").fadeOut("1000");

        var login;
        var pass;

        $('#btn-activate').click(
            function(e) {
                $('#btn-activate').attr("disabled", true);
                $(".loader").fadeIn("1000");
                var activation = $('#activation').val();
                var data = {
                      "activation" : activation
                };

                appRoutes.controllers.LoginTransfer.activationConnection().ajax({
                    data : JSON.stringify(data),
                    contentType : 'application/json',
                    success : function (data) {
                        if(data.code == 200){
                            console.log(data);
                            $(location).attr('href', "/home");
                        }
                        else {
                            $('#erreur').html('');
                            $('#erreur').fadeIn();
                            $('#erreur').append(data.message);
                        }
                        console.log(data);
                        $(".loader").fadeOut("1000");
                        $('#btn-activate').attr("disabled", false);

                    },
                     error: function (data) {
                         console.log(data);
                    }
                });
            });

        $( ".activation").keydown(function( event ) {
            if(event.keyCode == 13){
                $('#btn-activate').attr("disabled", true);
                $(".loader").fadeIn("1000");
                var activation = $('#activation').val();

                var data = {
                      "activation" : activation
                };

                appRoutes.controllers.LoginTransfer.activationConnection().ajax({
                    data : JSON.stringify(data),
                    contentType : 'application/json',
                    success : function (data) {
                        if(data.code == 200){
                            console.log(data);
                            $(location).attr('href', "/home");
                        }
                        else {
                            $('#erreur').html('');
                            $('#erreur').fadeIn();
                            $('#erreur').append(data.message);
                        }
                        console.log(data);
                        $(".loader").fadeOut("1000");
                        $('#btn-activate').attr("disabled", false);

                    },
                     error: function (data) {
                         console.log(data);
                    }
                });

              }
            });



    });
