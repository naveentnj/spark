/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.sql.connect.client.arrow

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

import org.apache.arrow.vector.{FieldVector, VectorSchemaRoot}
import org.apache.arrow.vector.complex.StructVector

private[arrow] object ArrowEncoderUtils {
  object Classes {
    val ITERABLE: Class[_] = classOf[scala.collection.Iterable[_]]
    val JLIST: Class[_] = classOf[java.util.List[_]]
  }

  def isSubClass(cls: Class[_], tag: ClassTag[_]): Boolean = {
    cls.isAssignableFrom(tag.runtimeClass)
  }

  def unsupportedCollectionType(cls: Class[_]): Nothing = {
    throw new RuntimeException(s"Unsupported collection type: $cls")
  }
}

trait CloseableIterator[E] extends Iterator[E] with AutoCloseable

private[arrow] object StructVectors {
  def unapply(v: AnyRef): Option[(StructVector, Seq[FieldVector])] = v match {
    case root: VectorSchemaRoot => Option((null, root.getFieldVectors.asScala.toSeq))
    case struct: StructVector => Option((struct, struct.getChildrenFromFields.asScala.toSeq))
    case _ => None
  }
}
