class Matrix(val width: Int, val height: Int, filling: List<Float>) {
    private val storage: MutableList<MutableList<Float>> = mutableListOf()

    constructor(width: Int, height: Int) : this(width, height, List(width * height) { 0f })

    init{
        var c = 0
        for (i in 0 until width){
            storage.add(mutableListOf<Float>())
            for (j in 0 until height){
                storage[i].add(filling[c++])
            }
        }
    }

    operator fun plus(other: Matrix): Matrix {
        if (width != other.width || height != other.height) {
            throw IllegalArgumentException("Matrices must have equal dimensions.")
        }
        val result = Matrix(width, height)
        for (i in 0 until width) {
            for (j in 0 until height) {
                result[i, j] = this[i, j] + other[i, j]
            }
        }
        return result
    }


    /**
     * Инвертировать заданную матрицу.
     * При инвертировании знак каждого элемента матрицы следует заменить на обратный
     */
    operator fun unaryMinus(): Matrix {
        val result = Matrix(width, height)
        for (i in 0 until width) {
            for (j in 0 until height) {
                result[i, j] = -this[i, j]
            }
        }
        return result
    }

    /**
     * Перемножить две заданные матрицы друг с другом.
     * Матрицы можно умножать, только если ширина первой матрицы совпадает с высотой второй матрицы.
     * В противном случае бросить IllegalArgumentException.
     * Подробно про порядок умножения см. статью Википедии "Умножение матриц".
     */
    operator fun times(other: Matrix): Matrix {
        if (height != other.width) {
            throw IllegalArgumentException("Inappropriate matrices dimensions.")
        }
        val result = Matrix(width, other.height)
        for (i in 0 until result.width) {
            for (j in 0 until result.height) {
                for (k in 0 until height) {
                    result[i, j] += this[i, k] * other[k, j]
                }
            }
        }
        return result
    }

    operator fun get(row: Int, column: Int): Float = storage[row][column]


    operator fun set(row: Int, column: Int, value: Float) {
        storage[row][column] = value
    }


    override fun toString(): String {
        val maxLength = storage.maxOf { x -> x.maxOf { y -> y.toString().length } }
        val sb = StringBuilder()
        for (i in 0 until width){
            for (j in 0 until height){
                sb.append(storage[i][j].toString().padStart(maxLength + 1))
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (width != other.width) return false
        if (height != other.height) return false

        for (i in 0 until width){
            for (j in 0 until height){
                if(get(i,j) != other.get(i,j)){
                    return false
                }
            }
        }

        return true
    }
}