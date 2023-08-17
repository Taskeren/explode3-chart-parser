package cn.taskeren.explode3.parser

interface Transformable<Container, Entity> {

	fun transform(block: (Entity) -> Entity): Container

}