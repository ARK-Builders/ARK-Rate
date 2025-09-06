package dev.arkbuilders.rate.core.domain.usecase

class ValidateGroupNameUseCase {
    operator fun invoke(name: String): Boolean {
        return name.isNotEmpty()
    }
}
