How to get it running:
Download and install java version >=21 and add it to your environment variables path.
Download and install maven and add it to your environment variables path.
To start the backend, run "mvn clean install" and "mvn exec:java".
To start the frontend, run "npm i" and "npm start" from the frontend folder.
This project is currently configured for local testing, but can be converted to live hosting.


Project Information:
Developer: Jude Lieb
Start date: 06/2023
End Date: Estimated 06/2028


Components and Features:

Piece/Square integer notation:
    Ranges from 0-12, with 0 = empty square, white pawn = 1; each integer corresponds to one of the 6 piece types (pawn, knight, bishop, rook, queen, king) for both black and white.

Board representation:
    The current chess board state is stored as an 8x8 matrix of integers using the above piece mapping

Coordinate Protocol:
    This application uses two coordinate classes. The first is the basic coordinate, with y and x values representing either a square in the board representation or a vector indicating the change in a piece's location.
    The second is a coordinate pair or CrdPair. This has x and y values that refer to the starting and ending position of a piece in a move. A special status integer is also included to indicate the condition of the En-Passant rule.

Board Initialization:
    The board is initialized from an array that holds the starting value of each square on the board.

Piece Move Set Initialization:
    Each piece is assigned an array of Crd objects; each is a vector along which the given piece is of the nature to move. These vectors are read from a .txt file and placed in instances of the Piece class.

Move Stack:
    The move history of a game instance is stored in a stack. This is essential for uses such as move undoing.

Turn Status (Color):
    The current player's turn is stored in a color integer. If it is white's turn, color will be of value (1-6) For black, its value is anything within (7-12). The colorSwap and colorCompare methods are used to control the turn and which pieces can be selected. The value of color will affect which pieces are taken into account for move legality and selection.

Move Legality Checking:
    To make a move, two squares must be selected, a start and end. These are stored in a CrdPair. This functionality allows all legal moves in a position to be identified. Move legality is checked through several steps. These include valid boundaries, vector matching, and check status review. Special moves have dedicated handler methods.

Pawn Promotion:
    Once a pawn reaches the far rank, it promotes to one of 4 other pieces. This is triggered by the promote int value, which is initially 0, but is replaced by the piece int value that it will be promoted to. (from the webpage promotion toggle)

Castling:
    Castling requires multiple conditions to be met. Changes in these conditions are tracked in the Move class and the current states saved in the Grid class. Move legality methods do the rest.

En-Passant:
    En-Passant breaks out of the usual capturing protocols. En-Passant requires special methods to handle it separately. Conditions such as the rank location of the pawn, an adjacent pawn, it having been moved there in the last turn while traversing 2 squares. 

Move Responses:
    A bare-bones move response system is running that chooses a legal move to play, based on material totals at a low depth. 

Web Interface:
    Websockets are used to connect the java server to the node app and from the node app to the client.
    The web app displays the selected piece's square and those to which it can legally move.