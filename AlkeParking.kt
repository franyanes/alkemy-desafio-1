import java.util.*

const val MILLISECONDS_IN_MINUTES = 60000
const val PARKING_MAX_SPACE = 20
const val TIME_FRACTION_COST = 5
const val DISCOUNT_PERCENTAGE = 0.15

enum class VehicleType(val fee: Int) {
    AUTO(20),
    MOTO(15),
    MINIBUS(25),
    BUS(30),
}

data class Parking(val vehicles: MutableSet<Vehicle>) {
    var vehiclesAndWinnings = Pair(0, 0)

    fun addVehicle(v: Vehicle): Boolean {
        if (vehicles.size >= PARKING_MAX_SPACE || vehicles.contains(v)) {
            println("Sorry, the check-in failed")
            return false
        }
        v.checkInTime = Calendar.getInstance()
        vehicles.add(v)
        println("Welcome to AlkeParking!")
        return true
    }

    fun checkOutVehicle(
        _plate: String,
        onSuccess: (bill: Int) -> Nothing,
        onError: () -> Nothing
    ) {
        val v = getVehicleOfPlate(_plate)
        if (v != null) {
            val fee = calculateFee(v.type, v.parkedTime, v.discountCard != null)
            onSuccess(fee)
            vehicles.remove(v)
            vehiclesAndWinnings = Pair(vehiclesAndWinnings.first + 1, vehiclesAndWinnings.second + fee)
            println("Your fee is ${fee}. Come back soon.")
        } else {
            println("Sorry, the check-out failed")
            onError()
        }
    }

    fun showWinnings() {
        println("${vehiclesAndWinnings.first} vehicles have checked out and have earnings of $${vehiclesAndWinnings.second}")
    }

    fun showVehiclesPlates() {
        println("Showing the plates of the parked vehicles:")
        for (v in vehicles) {
            println(v.plate)
        }
    }

    private fun getVehicleOfPlate(_plate: String): Vehicle? {
        for (v in vehicles) {
            if (v.plate == _plate) {
                return v
            }
        }
        return null
    }

    private fun calculateFee(type: VehicleType, parkedTime: Long, hasDiscountCard: Boolean): Int {
        var price = 0
        val timeFractions = (parkedTime - 2 * 60 * MILLISECONDS_IN_MINUTES) % 15 * MILLISECONDS_IN_MINUTES
        price += when (type) {
            VehicleType.AUTO -> 20
            VehicleType.MOTO -> 15
            VehicleType.MINIBUS -> 25
            VehicleType.BUS -> 30
        }
        price += (timeFractions * TIME_FRACTION_COST).toInt()
        if (hasDiscountCard) {
            price -= (price * DISCOUNT_PERCENTAGE).toInt()
        }
        return price
    }
}

data class Vehicle(val _plate: String, val _type: VehicleType, val _discountCard: String?) {
    val plate = _plate
    val type = _type
    lateinit var checkInTime: Calendar
    val discountCard: String? = null
    val parkedTime: Long
        get() = (Calendar.getInstance().timeInMillis - checkInTime.timeInMillis) / MILLISECONDS_IN_MINUTES

    override fun equals(other: Any?): Boolean {
        if (other is Vehicle) {
            return this.plate == other.plate
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return this.plate.hashCode()
    }
}
