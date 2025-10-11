dependencies {
    implementation("org.reflections:reflections:0.10.2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
tasks.withType<JavaExec>().configureEach {
    jvmArgs("-Dfile.encoding=UTF-8")
}
tasks.withType<Test>().configureEach {
    jvmArgs("-Dfile.encoding=UTF-8")
}
