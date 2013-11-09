module.exports=Ticket;
var crypto=require('crypto');
var fs=require('fs');
var privKey=fs.readFileSync('./keys/rsa_priv.pem').toString();
var pubKey=fs.readFileSync('./keys/rsa_pub.pem').toString();
var db=null;
var timelimit=[15,30,60];
var prices=[50,90,150];

function Ticket(){
}
Ticket.setDB=function(dbc){
    db=dbc;
}

Ticket.processRequest=function(req,res){
    var t1=Number(req.body.t1);
    var t2=Number(req.body.t2);
    var t3=Number(req.body.t3);
    var t1_bought=0;
    var t2_bought=0;
    var t3_bought=0;
    var tickets=[];
    //Falta adicionar à base de dados e limitar a 10 por user comecar por verificar quantos tem cada user
    if(!isNaN(t1)){
        for(var i=0; i<t1 && i<10 && req.user.t1+i<10;i++){
            var t=Ticket.createAndSign(req.user,1);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);
            t1_bought++;
        }
    }
    if(!isNaN(t2)){
        for(var i=0; i<t2 && i<10 && req.user.t2+i<10;i++){
            var t=Ticket.createAndSign(req.user,2);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);
            t2_bought++;
        }
    }
     if(!isNaN(t3)){
        for(var i=0; i<t3 && i<10 && req.user.t3+i<10;i++){
            var t=Ticket.createAndSign(req.user,3);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);
            t3_bought++;
        }
    }
    var totalCost=t1_bought*prices[0]+prices[1]*t2_bought+prices[2]*t3_bought;
    if(t1_bought+t2_bought+t3_bought>=10){
        if(t1_bought>0){
            var t=Ticket.createAndSign(req.user,1);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);

        }else if(t2_bought>0){
            var t=Ticket.createAndSign(req.user,2);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);

        }else if(t3_bought>0){
            var t=Ticket.createAndSign(req.user,3);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);

        }
    }
    res.send(JSON.stringify({'tickets':tickets,'total':totalCost}));
}

Ticket.calculatePrice=function(req,res){
    var t1=Number(req.body.t1);
    var t2=Number(req.body.t2);
    var t3=Number(req.body.t3);
    if(t1+req.user.t1>=10){
        t1=10-req.user.t1;
    }
    if(t2+req.user.t2>=10){
        t2=10-req.user.t2;
    }
    if(t3+req.user.t3>=10){
        t3=10-req.user.t3;
    }
    var t1_gift=0;
    var t2_gift=0;
    var t3_gift=0;
    if(t1+t2+t3>=10){
        if(t1>0){
            t1_gift++;
        }else if(t2>0){
            t2_gift++;
        }else if(t3>0){
            t3_gift++;
        }
    }
    var totalCost=t1*prices[0]+t2*prices[1]+t3*prices[2];
    res.send(JSON.stringify({'t1':t1,'t2':t2,'t3':t3,'t1_gift':t1_gift,'t2_gift':t2_gift,'t3_gift':t3_gift,'total':totalCost,'prices':prices}));
}

Ticket.getAllValidTickets=function(req,res){
    console.log('get Tickets');
    if(req.user==null){
        res.status(403);
        return;
    }else{
        db.all("Select User.id as user,user.devID as device,ticket.type as type,ticket.buyDate as time,ticket.ticketCode as signature from User,Ticket "+
               "where user.id=ticket.userid and user.id=? and ticket.useDate is NULL",req.user.id,
        function(err,data){
            if(err){
                res.send(JSON.stringify(err));
            }else{
                res.send(JSON.stringify(data));
            }
        });
    }
}

Ticket.getAllValitedTicketsForBus=function(req,res){
    console.log('get Tickets for BUS');
    if(req.user==null|| (req.user!=null && req.user.conductor<=0)){
        res.status(403);
        return;
    }else{
        if(req.body.busID==null){
            res.status(400).send(JSON.stringify({'busID':false}));
            return;
        }
        db.all("Select User.id as user,user.devID as device,ticket.type as type,ticket.buyDate as time,ticket.ticketCode as signature,useDate from User,Ticket "+
               "where user.id=ticket.userid and user.id=? and ticket.useDate is not NULL and ticket.useBus=? and ticket.useDate>?",req.user.id,req.body.busID,
        (new Date()).setHours(0).setMinutes(0).setSeconds(0).getMilliseconds(0).getTime(),
        function(err,data){
            if(err){
                res.send(JSON.stringify(err));
            }else{
                res.send(JSON.stringify(data));
            }
        });
    }
}

Ticket.createAndSign=function(user,type){
    var sign=crypto.createSign('RSA-SHA256');
    //var sign=new KJUR.crypto.Signature({"alg": "SHA1withRSA"});
    //sign.init(privKey);
    var time=new Date().getTime();
    sign.update(''+user.id+'-'+type+'-'+time);
    //sign.updateString(''+user.id+'-'+type+'-'+time);
    //add user deviD
    //var signature=sign.sign();
    var signature=sign.sign(privKey,'base64');
    console.log(''+user.id+'-'+type+'-'+time);
    console.log('Key: '+signature);
    /*var verify=new KJUR.crypto.Signature({"alg": "SHA1withRSA"});
    verify.init(pubKey);
    verify.updateString(''+user+'-'+type+'-'+time);
    console.log('Verificacao: ' + verify.verify(signature));*/
    return {'user': user.id,'device':user.dev,'type':type,'time':time,'signature':signature/*(new Buffer(signature,'hex')).toString('base64')*//*sign.sign(privKey,'base64')*/};
}

Ticket.validate=function(req,res,next){
    if(req.bus){
        res.status(401).send('');
        next();
        return;
    }else{
         var verifySign=crypto.createVerify('RSA-SHA256');
        if(typeof req.body.ticket == "string"){
            req.body.ticket=JSON.parse(req.body.ticket);
        }
        verifySign.update(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        console.log(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        var descodified=verifySign.verify(pubKey,req.body.ticket.signature,'base64');
        if(descodified){
            var time=(new Date()).getTime();
            db.run("Update Ticket set useDate=?,useBus=? where userid=? and buyDate=? and type=? and useDate is NULL;",time,req.bus.id,req.body.ticket.user,req.body.ticket.time,req.body.ticket.type,function(err){
                if(err){
                    res.status(500).send(JSON.stringify(err));
                }else{
                    if(this.changes==0){
                        res.status(400).send(JSON.stringify({'valid':0,'reason':'Not found or used'}));
                    }else{
                        req.body.ticket.useDate=time;
                        res.send(JSON.stringify(req.body.ticket));
                    }
                }
            });
        }else{
            res.status(400).send(JSON.stringify({'valid':0,'reason':'fake','msg':'Fake Ticket'}));
        }
    }
}


Ticket.iSvalidateTicket=function(req,res,next){
    if(req.user==null || (req.user!=null && req.user.conductor<=0)){
        res.status(403).send('');
        next();
        return;
    }else{
        var verifySign=crypto.createVerify('RSA-SHA256');
        // var verify=new KJUR.crypto.Signature({"alg": "SHA1withRSA"});
         //verify.init(pubKey);

        if(typeof req.body.ticket == "string"){
            req.body.ticket=JSON.parse(req.body.ticket);
        }
        //verify.updateString(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        verifySign.update(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        console.log(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        //console.log((new Buffer(req.body.ticket.signature,'base64')).toString('hex'));
        //verifySign.update(req.body.ticket.signature);
        //var descodified=verify.verify((new Buffer(req.body.ticket.signature,'base64')).toString('hex'));
        var descodified=verifySign.verify(pubKey,req.body.ticket.signature,'base64');
        console.log(descodified);
        if(descodified){
            db.get("Select * from Ticket where userid=? and buyDate=? and type=?;",req.body.ticket.user,req.body.ticket.time,req.body.ticket.type,function(err,row){
                if(err){
                    res.status(500).send(JSON.stringify(err));
                }else{
                    if(row){
                        if(row.useDate){
                            var timeNow=(new Date()).getTime();
                            var timediff=timeNow-row.useDate;
                            if(timediff>=0 && (timediff/1000/60)<timelimit[row.type-1]){
                                res.send(JSON.stringify({'valid':1,'timeLeft':timelimit[row.type-1]-(timediff/1000/60)}));
                            }else{
                                res.send(JSON.stringify({'valid':0,'reason':'expired','timeLeft':timelimit[row.type-1]-(timediff/1000/60)}));
                            }
                            //res.send(JSON.stringify({'valid':1,''}))

                        }else{
                            res.send(JSON.stringify({'valid':0,'reason':'Not used'}));
                        }
                    }else{
                        res.send(JSON.stringify({'valid':0,'reason':'Not found'}));
                    }
                }
            });
        }else{
            res.status(400).send(JSON.stringify({'valid':0,'reason':'fake','msg':'Fake Ticket'}));
        }
    }
}
