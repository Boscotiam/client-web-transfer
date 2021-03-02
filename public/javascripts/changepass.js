/**
 * Created by bosco on 25/02/2015.
 */
$(document)
    .ready(
    function(e) {
         $(".loader").fadeOut("1000");

        $('#saveChangePWD').click(function (e) {
            //console.log("verifyChangePWD");
            verifyChangePWD();
        });

        $("#oldPWD" ).keydown(function( event ) {
            if(event.keyCode == 13){
                verifyChangePWD()
            }
        });

        $("#newPWD" ).keydown(function( event ) {
            if(event.keyCode == 13){
                verifyChangePWD()
            }
        });
        $("#reNewPWD" ).keydown(function( event ) {
            if(event.keyCode == 13){
                verifyChangePWD()
            }
        });



        function verifyChangePWD(){
            var oldPWD = $('#oldPWD').val();
            var newPWD = $('#newPWD').val();
            var reNewPWD = $('#reNewPWD').val();
            if (oldPWD == '') {
                $('#erreur').html('');
                $('#erreur').fadeIn();
                $('#erreur').append(labelVerifyOld);
                $('#saveChangePWD').attr("disabled", false);
            }
            else if (newPWD == '') {
                $('#erreur').html('');
                $('#erreur').fadeIn();
                $('#erreur').append(labelVerifyNew);
                $('#saveChangePWD').attr("disabled", false);
            }
            else if (reNewPWD == '') {
                $('#erreur').html('');
                $('#erreur').fadeIn();
                $('#erreur').append(labelVerifyRenew);
                $('#saveChangePWD').attr("disabled", false);
            }
            else if (newPWD != reNewPWD) {
                $('#erreur').html('');
                $('#erreur').fadeIn();
                $('#erreur').append(labelVerifyNewSame);
                $('#saveChangePWD').attr("disabled", false);
            }
            else{
                var data = {
                    'oldmdp' : oldPWD,
                    'mdp' : newPWD
                };
                doSaveChangePWD(data);
            }
        }

        function doSaveChangePWD(data){
            $(".loader").fadeIn("1000");
            $('#saveChangePWD').attr("disabled", true);
            appRoutes.controllers.UserController.changePassword().ajax({
                data : JSON.stringify(data),
                contentType : 'application/json',
                success : function (json) {
                    if (json.code == 200) {
                        $(location).attr('href',
                            "/");
                    }
                    else{
                        $('#erreur').html('');
                        $('#erreur').fadeIn();
                        $('#erreur').append(json.message);
                    }
                    $(".loader").fadeOut("1000");
                    $('#saveChangePWD').attr("disabled", false);
                },
                error: function (xmlHttpReques,chaineRetourne,objetExeption) {
                    if(objetExeption == "Unauthorized"){
                        $(location).attr('href',"/");
                    }

                    $(".loader").fadeOut("1000");
                    $('#saveChangePWD').attr("disabled", false);
                }
            });
        }


    });
