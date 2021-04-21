package com.utamas.appointments.architecture.annotations

import com.utamas.appointments.architecture.abstractions.BaseViewModel
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DeclareViewModel(val value: KClass<out BaseViewModel>)