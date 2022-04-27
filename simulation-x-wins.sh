GAME_ID=$1
./game-cli.sh game-move -g $GAME_ID -p x --row 0 --col 0
sleep 1
./game-cli.sh game-move -g $GAME_ID -p o --row 0 --col 1
sleep 1
./game-cli.sh game-move -g $GAME_ID -p x --row 1 --col 0
sleep 1
./game-cli.sh game-move -g $GAME_ID -p o --row 0 --col 2
sleep 1
./game-cli.sh game-move -g $GAME_ID -p x --row 2 --col 0

