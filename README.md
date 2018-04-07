# Connect-Four
A simple connect four app to play around with game AI.

## Gettings started
Making your own AI is simple, all you need to do is implement [`AI.kt`](./app/src/main/java/me/nickellis/connectfour/ai/AI.kt)
and add it to the [`PlayerList.kt`](./app/src/main/java/me/nickellis/connectfour/PlayerList.kt) There's an example of how to 
do this, [`Dummy.kt`](./app/src/main/java/me/nickellis/connectfour/ai/Dummy.kt).

```kotlin
class Dummy(piece: Piece) : Player(piece, false), AI {

  override suspend fun makeMove(board: ReadOnlyBoard): Int {
    return (0 until board.numOfCols())
      .indexOfFirst { board.getColumn(it).size < board.numOfRows() }
  }

  override fun toString(): String = "Dummy"
}
```

You'll notice that the make move function has the modifier `suspend`, so your method can take as long as it'd like. If you're
not familiar with Kotlin coroutines, I recommend reading through this [guide](https://github.com/Kotlin/kotlinx.coroutines/blob/master/coroutines-guide.md).
