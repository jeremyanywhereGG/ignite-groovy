// Groovy Cube Solver
// #cube is numbered thus.
// #front face from top left to bottom right in three rows
// #    0,1,2
// #    3,4,5
// #    6,7,8
// # then along the top from top left.. anti clock wise, around to the left top. 
// # 9-20
// # the back face, done the same, but in mirror image, or as if you were looking through \
// # the front face if it was transparent 
// # 21,22,23
// # 24,25,26,
// # 27,28,29  etc.
// # then along the top from the top left (looking through the front face) anti clock
// # around to left top
// # 30-41
// #
cube = (0..41).toList()
MAX_DEPTH = 6
TOP_ROW = [9, 10, 11, 12, 30, 31, 32, 33, 0, 1, 2, 20, 41, 21, 22, 23]
//all_positions = (0..MAX_DEPTH).collect { [] as ArrayList }
solutions = [] as ArrayList<ArrayList<String>>

moves = ['front_anticlock', 'left_col_rot', 'back_anticlock', 'front_180', 'top_row_rot',
             'middle_row_rot', 'right_col_rot', 'back_180', 'front_clock', 'middle_col_rot',
             'bottom_row_rot', 'back_clock']

move_dict = [
    'front_clock': [[0, 6], [1, 3], [2, 0], [3, 7], [4, 4], [5, 1], [6, 8], [7, 5], [8, 2], [9, 18],
                    [10, 19], [11, 20], [12, 9], [13, 10], [14, 11], [15, 12], [16, 13], [17, 14], [18, 15], [19, 16], [20, 17]],
    'front_anticlock': [[0, 2], [1, 5], [2, 8], [3, 1], [5, 7], [6, 0], [7, 3], [8, 6], [9, 12], [10, 13],
                        [11, 14], [12, 15], [13, 16], [14, 17], [15, 18], [16, 19], [17, 20], [18, 9], [19, 10], [20, 11]],
    'front_180': [[0, 8], [1, 7], [2, 6], [3, 5], [5, 3], [6, 2], [7, 1], [8, 0], [9, 15], [10, 16],
                  [11, 17], [12, 18], [13, 19], [14, 20], [15, 9], [16, 10], [17, 11], [18, 12], [19, 13], [20, 14]],
    'back_clock': [[21, 27], [22, 24], [23, 21], [24, 28], [25, 25], [26, 22], [27, 29], [28, 26], [29, 23], [30, 39],
                   [31, 40], [32, 41], [33, 30], [34, 31], [35, 32], [36, 33], [37, 34], [38, 35], [39, 36], [40, 37], [41, 38]],
    'back_anticlock': [[21, 23], [22, 26], [23, 29], [24, 22], [26, 28], [27, 21], [28, 24], [29, 27], [30, 33], [31, 34],
                       [32, 35], [33, 36], [34, 37], [35, 38], [36, 39], [37, 40], [38, 41], [39, 30], [40, 31], [41, 32]],
    'back_180': [[21, 29], [22, 28], [23, 27], [24, 26], [26, 24], [27, 23], [28, 22], [29, 21], [30, 36], [31, 37],
                 [32, 38], [33, 39], [34, 40], [35, 41], [36, 30], [37, 31], [38, 32], [39, 33], [40, 34], [41, 35]],
    'top_row_rot': [[9, 32], [10, 31], [11, 30], [30, 11], [31, 10], [32, 9], [0, 23], [1, 22], [2, 21], [23, 0],
                    [22, 1], [21, 2], [20, 33], [12, 41], [33, 20], [41, 12]],
    'middle_row_rot': [[3, 26], [4, 25], [5, 24], [26, 3], [25, 4], [24, 5], [13, 40], [19, 34], [34, 19], [40, 13]],
    'bottom_row_rot': [[6, 29], [7, 28], [8, 27], [29, 6], [28, 7], [27, 8], [15, 38], [16, 37], [17, 36], [38, 15],
                       [37, 16], [36, 17], [14, 39], [36, 18], [39, 14], [18, 36]],
    'left_col_rot': [[18, 41], [19, 40], [20, 39], [41, 18], [40, 19], [39, 20], [0, 27], [3, 24], [6, 21], [27, 0],
                     [24, 3], [21, 6], [9, 38], [38, 9], [30, 17], [17, 30]],
    'middle_col_rot': [[1, 28], [4, 25], [7, 22], [28, 1], [25, 4], [22, 7], [10, 37], [37, 10], [31, 16], [16, 31]],
    'right_col_rot': [[12, 35], [13, 34], [14, 33], [35, 12], [34, 13], [33, 14], [2, 29], [5, 26], [8, 23], [29, 2],
                      [26, 5], [23, 8], [11, 32], [32, 11], [36, 15], [15, 36]]
]
def applyMove(move, currentPos) {
    def newPos = currentPos.clone()
    move_dict[move].each { swap ->
        newPos[swap[0]] = currentPos[swap[1]]
    }
    return newPos
}
// Generate all valid combinations of depth 2 moves (e.g. ['right_col_rot', 'back_180'] or ['front_clock', 'middle_col_rot'])
//TEST method. This doesn't need to be in the final script.
def generateDepth2s() {
    permList = []
    moves.each { first ->
      moves.each { second ->
        if (first != second) {
            permList.add( [first, second] )
        }
      }
   }
   return permList
}

// Checks if the first two moves are "useless", like moving the front face and then the back face
def isBadPairing(pair) {
    def badPairings = ['_anticlock', '_clock', 'back', 'front', 'top', 'middle_col', 'middle_row', 'right', 'left', 'col_rot', 'row_rot']
    def badCombos = [['back', 'front'], ['bottom', 'top'], ['left', 'right']]
    for (p = 0; p < badPairings.size();p++) {
        if (pair[0].contains(badPairings[p]) && pair[1].contains(badPairings[p])) {
            return true
        }
    }
    for (c = 0; c < badCombos.size();c++) {
        if ((pair[0].contains(badCombos[c][0]) && pair[1].contains(badCombos[c][1])) || (pair[0].contains(badCombos[c][1]) && pair[1].contains(badCombos[c][0]))) {
            return true
        }
    }
    return false
}

// This would be normally done outside of this script except for testing. The actual task is "find_path_with_two_move etc."
def startFromDepth2(source, target, depth2s) {
    println "Starting the two move start pairs.. ${depth2s.size()}"
    depth2s.each { pair ->
        findPathWithTwoMoveStart(pair, source, target)
    }

}

def compare(source, target) {
    if (source.size() != target.size()) {
        println("Lengths not equal ${source.size()}, ${target.size()}")
        return false
    }
    for (int i = 0; i < target.size(); i++) {
        if (source[i] != target[i] && target[i] != '?') {
            return false
        }
    }
    return true
}

// Recursive. Does the main work. This needs to be called as a single task. A pair of moves are passed
// with the assumption that they are unique in the problem space. 
def findPathWithTwoMoveStart(pairOfMoves, source, target) {
    if (isBadPairing(pairOfMoves)) {
        return false // reject bad moves straight away.
    }
    def res = applyMove(pairOfMoves[0], source)
    def res2 = applyMove(pairOfMoves[1], res)
    findPath(2, res2, target, pairOfMoves)
}

def findPath(depth, source, target, breadcrumbs) {
    if (depth > MAX_DEPTH) {
        return false
    }
    // Step through all possible moves, if we have the target in any case, stop iterating and return.
    // If we don't have the target, make the move, add to breadcrumbs, and recurse.
    // TODO, also create a parameter with a list of lists which is all the aggregated solutions.
    for (move in move_dict.keySet()) {
        if (isSkippable(move, breadcrumbs)) {
            continue
        }
        if (isRedundant(breadcrumbs + [move])) {
            continue
        }
        def res = applyMove(move, source)
        if (compare(res, target)) {
            solution = [] as ArrayList<String>
            for (int i = 0; i < breadcrumbs.length; i++) {
                solution.add(breadcrumbs[i])
            }
            solution.add(move)
            solutions.add(solution)
            println("Type of solutions is $solutions.class")
            println("${breadcrumbs.size() + 1}  ${breadcrumbs + [move]}")
            return true
        }
        findPath(depth + 1, res, target, breadcrumbs + [move])

    }
}

// If we have just moved a face (front or back), then moving that face again is pointless
def isSkippable(move, breadcrumbs) {
    if (breadcrumbs.size() < 1) {
        return false
    }
    def lastUnderscoreIndex = breadcrumbs[-1].lastIndexOf('_')
    def prefix = breadcrumbs[-1][0..lastUnderscoreIndex]
    // Don't do two moves the same
    if (breadcrumbs[-1] == move) {
        return true
    }
    // Don't do two consecutive front moves or back moves
    if ((prefix.contains("front") || prefix.contains("back")) && move.startsWith(prefix)) {
        return true
    }
    return false
}

// Paths with repeated front/back/front are redundant and will be equivalent to shorter paths, ultimately
def isRedundant(currentPath) {
    if (currentPath.size() < 3) {
        return false
    }
    if (currentPath[-1].startsWith("front") &&
        currentPath[-2].startsWith("back") &&
        currentPath[-3].startsWith("front")) {
        return true
    }
    if (currentPath[-1].startsWith("back") &&
        currentPath[-2].startsWith("front") &&
        currentPath[-3].startsWith("back")) {
        return true
    }
    if (currentPath[-1].startsWith("right") &&
        currentPath[-2].startsWith("left") &&
        currentPath[-3].startsWith("right")) {
        return true
    }
    if (currentPath[-1].startsWith("left") &&
        currentPath[-2].startsWith("right") &&
        currentPath[-3].startsWith("left")) {
        return true
    }
    return false
}

// Helps fill a cube with numbers
def fill(lst, squares) {
    squares.each { c ->
        lst[c] = c
    }
}
def test1() {
    // Fill with -1, so those squares don't matter
    def source = (0..41).collect { -1 }
    fill(source, [13, 34]) // Front right middle and back right middle
    fill(source, [])
    def target = source.clone()
    target[13] = source[34]
    target[34] = source[13]
    println("targ... is $target \nsource. is $source")
    source = applyMove('middle_row_rot', source)
    source = applyMove('left_col_rot', source)
    source = applyMove('middle_row_rot', source)
    //source = applyMove('right_col_rot', source)
    println("after rot: \ntarg... is $target - \nsource. is $source")
    println(compare(source, target))
    //find_path(0, source, target, [])
}

def test2() {
    // Fill with -1, so those squares don't matter
    def source = (0..41).collect { -1 }
    fill(source, [13, 34, 8]) // Front right middle and back right middle and lower left corner for constraint
    def target = source.clone()
    target[13] = source[34]
    target[34] = source[13]
    println("targ... is $target \nsource. is $source")
    startFromDepth2(source, target, generateDepth2s())
}

def fitEdgeBottomToSide() {
    // Fill with -1, so those squares don't matter
    def source = (0..41).collect { -1 }
    // Try to fit edge on second row (from 7 -> 13), so the top face needs to stay the same, and the middle row apart from the corner.
    fill(source, [0, 1, 2, 21, 22, 23, 9, 10, 11, 30, 31, 32, 20, 41, 3, 4, 5, 24, 25, 26, 19, 40, 7])
    def target = source.clone()
    target[5] = source[7]
    target[7] = '?'
    println("targ... is $target \nsource. is $source")
    println("Gdumping..")
    println("    G pair..${['front_anticlock', 'middle_col_rot']}")
    println("    G source..${target}")
    println("    G target..${source}")
    startFromDepth2(source, target, generateDepth2s())
}

def swapLast7And5() {
    def source = (0..41).toList()
    // Swap last remaining 7 and 5
    def target = source.clone()
    target[5] = source[7]
    target[7] = source[5]
    println("targ is... is $target \nsource is.. $source")
    startFromDepth2(source, target, generateDepth2s())
}

def mapToSelf() {
    def source = (0..41).toList()
    def target = source.clone()
    println("targ... is $target \nsource. is $source")
    findPath(0, source, target, [])
}

// Function definitions for fill, apply_move, and compare go here

def test5() {
    def source = (0..41).toList()
    // Swap last remaining 7 and 5
    def target = source.clone()
    target[7] = source[28]
    target[28] = source[7]
    target[16] = source[37]
    target[37] = source[16]
    println("targ... is $target \nsource. is $source")
    findPath(0, source, target, [])
}

def go(runner) {
    println("Running method: $runner()")
    //println("Starting with depth: $MAX_DEPTH..")
    def start_time = System.currentTimeMillis()
    //mapToSelf()
    this."$runner"()
    def elapsed_time = System.currentTimeMillis() - start_time
    // Convert the elapsed time to hours, minutes, and seconds
    def hours = (elapsed_time / 3600000) as int
    def minutes = ((elapsed_time % 3600000) / 60000) as int
    def seconds = ((elapsed_time % 60000) / 1000) as int
    // Print the elapsed time
    println("Elapsed time: $hours hrs $minutes mins $seconds seconds")
    return solutions // an array of arrays.
}
def runAsIgniteTask() {
    println("Starting Task No: $pTaskNo ..")
    println(" with  pair..${pPair}")
    // println("    source..${pSource}")
    // println("    target..${pTarget}")
    findPathWithTwoMoveStart(pPair, pSource, pTarget)
}

go('runAsIgniteTask')
//go('fitEdgeBottomToSide')
//println res


def findPaths(depth, source, target, move) {
    if (depth > MAX_DEPTH or isSilly(move)) {
        return false
    }
    def res = applyMove(move_dict[move], source)
    if (compare(res, target)) {
        solutions.add(solution)
        return true
    }
    for (each_move in move_dict) {
        findPaths(depth + 1, res, target, move_dict[each_move])
    }
}
