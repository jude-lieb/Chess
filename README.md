How to get it running:
Download and install java version >=21 and add it to your environment variables path.
Download and install maven and add it to your environment variables path.
To start the backend, run "mvn exec:java".
To start the frontend, run "npm i" and "npm start".


Project Information:
Developer: Jude Lieb
Start date: 06/2023
End Date: Estimated 06/2028


Completed Components and Features:

Piece/Square integer notation
    Ranges from 0-12, with 0 as empty square, white pawn = 1, each integer corresponsd to one of the 6 piece types (pawn, knight, bishop, rook, queen, king) for both black and white.

Board representation
    The current chess board state is stored as an 8x8 matrix of integers using the above piece mapping

Coordinate Protocol
    This application uses two coordinate style classes. The first is the basic coordinate, with y and x values representing either a square in the board representation or a vector indicating the change in a piece's location.

    The second is a coordinate pair or CrdPair. This has x and y values that refer to the starting and ending position of a piece in a move. A special status integer is also included to indicate the condition of the En-Passant rule.

Board Initialization
    The board is initialized from an array that hold the starting values of each square on the board.

Piece Move Set Initialization
    Each piece is assigned an array of Crd objects; each is a vector along which the given piece is of the nature to move. These vectors are read from a .txt file and placed in instances of the Piece class.

Move Stack
    The move history of a game instance is stored in a stack. This is essential for uses such as move undoing.

Turn Status (Color)
    The current player's turn is stored in a color integer. If it is white's turn, color will be of value (1-6) For black, its value is anything within (7-12). The colorSwap and colorCompare methods are used to control the turn and which pieces can be selected. The value of color will affect which pieces are taken into account for move legality and selection.

Move Legality Checking 
    To make a move, two squares must be selected, a start and end. These are stored in a CrdPair. This functionality allows all legal moves in a position to be identified. Move legality is checked through several steps: 
        First, the coordinate change from start to end must match one of the vectors from the chosen piece's move set. 
        Second, the destination square of the piece must lie within the bounds of the board.
        Third, the destination square cannot contain a piece of the same color.
        Fourth, the squares between the source and destination must be clear of pieces, except for knights.
        Fifth, The resulting board position may not allow the player's king to be in check. 
    Additional special moves that break these rules have dedicated methods to handle them.

Pawn Promotion
    Once a pawn reaches the far rank, it promotes to one of 4 other pieces. This is triggered by the promote int value, which is initially 0, but is replaced by the piece int value that it will be promoted to. (from the webpage promotion selector)

Castling
    Annoying. Castling requries multiple conditions:
        Not in check. All squares between king and rook must be empty. The king must not castle into check. The extra square traversed by the king cannot be under attack from any opposing piece. The king must have not moved yet in the game. The rook used must not have moved either.
    These conditions are tracked in the Move objects and saved in the Grid class. Move legality methods do the rest.

En-Passant
    ANNOYING. En-Passant is a rule to balance the 2 square first move ability of pawns that can sometimes allow for pawns to pass each other without a capturing opportunity. En-Passant breaks out of the usual capturing protocols. This requires special methods to handle it separately. Conditions such as the rank location of the pawn, an adjacent pawn, it having been moved there in the last turn while traversing 2 squares. 



In Progress Components and Features

Game State Determination
    After each turn, all legal moves are found. It is necessary to know if the position has reached one of the game ending conditions. The conditions are as follows:
        CheckMate: The king is checked and no single-move response is able to relieve the check.
        StaleMate: There are no legal moves, but the king is not under attack. (Draw)
        Repetition Draw: The same exact board state in reached three times with the same special parameters (same player's turn, same en-passant and castling status)
        Other draws/Forfeit/Time: Not a concern right now, maybe later

Web Interface
    Visual display of a board background, display of pieces

Incomplete Components and Features:


