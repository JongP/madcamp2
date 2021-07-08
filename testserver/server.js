const express = require('express');
const app = express();

app.get('/',(req,res)=>{
    res.sendFile(__dirname + "/index.html");
});
//app.close(3000);

app.listen(3002,()=>{
    console.log("the servrer is running on port 3000");
});