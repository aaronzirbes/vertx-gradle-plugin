import com.darylteo.vertx.gradle.util.ConfigBuilder
import groovy.json.JsonOutput

/**
 * Created by dteo on 29/08/2014.
 *
 * Note: these tests are based off the toString() methods of the various data types
 * and nested properties are ordered arbitrarily.
 */
class ConfigBuilderTests extends GroovyTestCase {
  void testPropertyAssignment() {
    ConfigBuilder config = new ConfigBuilder()
    assertEquals(config.getProperties(), [:])

    // setting properties directly
    config.foo = "bar"
    assertEquals(config.getProperties(), [foo: 'bar'])
    config.hello = "world"
    assertEquals(config.getProperties(), [foo: 'bar', hello: 'world'])

    // setting properties with closure
    config {
      foo = "bar2"
    }
    assertEquals(config.getProperties(), [foo: 'bar2', hello: 'world'])

    // setting multiple properties with closure
    config {
      foo = "bar3"
      hello = "world2"
    }
    assertEquals(config.getProperties(), [foo: 'bar3', hello: 'world2'])

    // nulling
    config.foo = null
    assertEquals(config.getProperties(), [foo: null, hello: 'world2'])
  }

  void testMethodAssignment() {
    ConfigBuilder config = new ConfigBuilder()
    assertEquals(config.getProperties(), [:])

    // setting properties directly
    config.foo "bar"
    assertEquals(config.getProperties(), [foo: 'bar'])
    config.hello "world"
    assertEquals(config.getProperties(), [foo: 'bar', hello: 'world'])

    // setting properties with closure
    config {
      foo "bar2"
    }
    assertEquals(config.getProperties(), [foo: 'bar2', hello: 'world'])

    // setting multiple properties with closure
    config {
      foo "bar3"
      hello "world2"
    }
    assertEquals(config.getProperties(), [foo: 'bar3', hello: 'world2'])

    // nulling
    config.foo null
    assertEquals(config.getProperties(), [foo: null, hello: 'world2'])
  }

  void testDataTypes() {
    ConfigBuilder config = new ConfigBuilder()
    assertEquals(config.getProperties(), [:])

    // string
    config.foo = "bar"
    assertEquals(config.getProperties(), [foo: 'bar'])
    assertEquals(config.foo.class, String.class)

    // int
    config.foo = 1
    assertEquals(config.getProperties(), [foo: 1])
    assertEquals(config.foo.class, Integer.class)

    // long
    config.foo = 1l
    assertEquals(config.getProperties(), [foo: 1])
    assertEquals(config.foo.class, Long.class)

    // float
    config.foo = 1.0f
    assertEquals(config.getProperties(), [foo: 1.0])
    assertEquals(config.foo.class, Float.class)

    // double - groovy turns these into BigDecimal
    config.foo = 1.0
    assertEquals(config.getProperties(), [foo: 1.0])
    assertEquals(config.foo.class, BigDecimal.class)

    // bool
    config.foo = true
    assertEquals(config.getProperties(), [foo: true])
    assertEquals(config.foo.class, Boolean.class)

    // array
    config.foo = [1, 2, 3]
    assertEquals(config.getProperties(), [foo: [1, 2, 3]])
    assert (List.class.isAssignableFrom(config.foo.class))

    // complex - this overwrites existing values
    config.foo {
      hello "world"
    }
    assertEquals(config.getProperties(), [foo: [hello: 'world']])
    assertEquals(config.foo.class, ConfigBuilder.class)
  }

  void testNestedAssignment() {
    ConfigBuilder config = new ConfigBuilder()
    assertEquals(config.getProperties(), [:])

    // standard
    config {
      foo 'bar'
    }
    assertEquals(config.getProperties(), [foo: 'bar'])

    // nested with closures
    config {
      inner {
        hello = 'world'
      }
    }
    assertEquals(config.getProperties(), [foo: 'bar', inner: [hello: 'world']])

    // nested with properties
    config {
      inner.hello2 = 'world2'
      inner.flag true
    }

    assertEquals(config.getProperties(), [foo: 'bar', inner: [flag: true, hello2: 'world2', hello: 'world']])

    config {
      map(
        key: 'value',
        nest: {
          another 'property'
        }
      )
    }

    assertEquals(config.getProperties(), [
      foo  : 'bar',
      inner: [
        flag: true, hello2: 'world2', hello: 'world'
      ],
      map  : [
        nest: [
          another: 'property'
        ],
        key : 'value'
      ]
    ])
  }

  void testExpando() {
    ConfigBuilder config = new ConfigBuilder()

    config {
      map(
        key: 'value',
        nest: {
          another 'property'
        }
      )
    }

    config.properties = 'hello'

    // in order to avoid reflection of properties,
    // use the accessor getProperties() to get the underlying Map
    assertNotSame('hello', config.getProperties());

    // ensure that we can convert this to Json.
    // since the result of the Json is non-deterministic, we shall just test for exceptions
    println JsonOutput.toJson(config)
  }
}
