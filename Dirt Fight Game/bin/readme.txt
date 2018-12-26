Changes:
-Characters can now effectively share the same square. It doesn't cost 
anything to go over another player.
-Newlines are replaces with semicolons so that you can use one readline.
-Send your name after you connect.
-You will need to be able to handle an infinite number of games.  After 
you get done with one game, I will just send you the field for the next 
game.  I won't tell you who won or anything.  I will just send you a
field and you send my a move.  Over and over and over.

Testing:
-Run DirtFightServer.
-Run two bots.  I have included two that work: MoveRightBot and
BombBehindBot.
-The server will tell you as they get connections.  After it gets two 
connections it will run those two bots against each other forever.  If
the animation doesn't start it is probably a disagreement in what input
and outputs the server is expecting.  Take a look at how MoveRightBot 
works to fix yours.

Final Day:
-I will provide a server that will accept as many connections as there
are bots.
-It will pair you up at different times.  Games should be pretty quick 
but a client could sit idle for up to a few minutes.
-If a client dies, I will have slot saved for your name so you can try
to reconnect and get back in the game.
-If your client dies, you lose the game.
-If your client doesn't respond in 1 second, you lose the game and I
drop your connect because you are probably stuck.
-I will produce rankings based on win records.
-80% for a better ranking than MoveRightBot.
-90% for a better ranking than BombBehindBot.
-90-100% based on where you are in the rankings above BombBehindBot.
-0% if below MoveRightBot  (You can fix and I can grade it later that night.)
