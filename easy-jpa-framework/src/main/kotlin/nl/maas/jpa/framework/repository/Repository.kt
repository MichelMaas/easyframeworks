package nl.maas.jpa.framework.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.persistence.metamodel.SingularAttribute
import nl.maas.jpa.framework.entity.AbstractEntity
import nl.maas.jpa.framework.entity.AbstractEntity_
import kotlin.reflect.KClass

abstract class Repository<T : AbstractEntity> {

    protected abstract val entityType: KClass<T>
    protected abstract var entityManager: EntityManager

    private lateinit var criteria: CriteriaBuilder
    private lateinit var query: CriteriaQuery<T>
    private lateinit var root: Root<T>

    fun fetch(id: Int): T {
        setUp()
        query.select(root).where(criteria.equal(root.get(AbstractEntity_.id), id))
        return entityManager.createQuery(query).singleResult
    }

    private fun setUp() {
        require(entityManager != null, { "Something went wrong instantiating the EntityManager" })

        criteria = entityManager.criteriaBuilder
        query = criteria.createQuery(entityType.java)
        root = query.from(entityType.java)
    }

    fun and(vararg expression: Predicate): nl.maas.jpa.framework.repository.Repository<T> {
        criteria.and(*expression)
        return this
    }

    fun or(vararg expression: Predicate): nl.maas.jpa.framework.repository.Repository<T> {
        criteria.or(*expression)
        return this
    }

    fun <Y> equals(attribute: SingularAttribute<T, Y>, value: Y): Predicate {
        return criteria.equal(root.get(attribute), value)
    }

    fun <Y> notEquals(attribute: SingularAttribute<T, Y>, value: Y): Predicate {
        return criteria.notEqual(root.get(attribute), value)
    }

    fun like(attribute: SingularAttribute<T, String>, value: String): Predicate {
        return criteria.like(root.get(attribute), value)
    }

    fun notLike(attribute: SingularAttribute<T, String>, value: String): Predicate {
        return criteria.notLike(root.get(attribute), value)
    }

    fun <Y> inValues(attribute: SingularAttribute<T, Y>, vararg value: Y): Predicate {
        val yIn = criteria.`in`(root.get(attribute))
        value.forEach { yIn.value(it) }
        return yIn
    }

    fun newQuery(): nl.maas.jpa.framework.repository.Repository<T> {
        setUp()
        return this
    }

    fun get(): List<T> {
        return entityManager.createQuery(query).resultList
    }

}