package com.unascribed.yttr.network.concrete;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.unascribed.yttr.network.concrete.DefaultMarshallers.ListMarshaller;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import com.unascribed.yttr.network.concrete.annotation.field.Optional;
import com.unascribed.yttr.network.concrete.exception.BadMessageException;

import com.google.common.base.Throwables;

import net.minecraft.network.PacketByteBuf;

class WireField<T> {
	private final Field f;
	private final MethodHandle getter;
	private final MethodHandle setter;
	private final Marshaller<T> marshaller;
	private final Class<T> type;
	private final boolean optional;
	
	public WireField(Field f) {
		f.setAccessible(true);
		this.f = f;
		try {
			getter = MethodHandles.lookup().unreflectGetter(f);
			setter = MethodHandles.lookup().unreflectSetter(f);
			type = (Class<T>) f.getType();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		MarshalledAs ma = f.getAnnotation(MarshalledAs.class);
		if (ma != null) {
			marshaller = DefaultMarshallers.getByName(ma.value());
		} else if (Marshallable.class.isAssignableFrom(type)) {
			marshaller = new MarshallableMarshaller(type);
		} else if (ImmutableMarshallable.class.isAssignableFrom(type)) {
			marshaller = new ImmutableMarshallableMarshaller(type);
		} else {
			if (type == List.class && f.getGenericType() instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType)f.getGenericType();
				if (pt.getActualTypeArguments().length == 1) {
					Type param = pt.getActualTypeArguments()[0];
					if (param instanceof Class) {
						Class<?> paramClazz = (Class<?>)param;
						if (Marshallable.class.isAssignableFrom(paramClazz)) {
							marshaller = new ListMarshaller(new MarshallableMarshaller(paramClazz));
						} else if (ImmutableMarshallable.class.isAssignableFrom(paramClazz)) {
							marshaller = new ListMarshaller(new ImmutableMarshallableMarshaller(paramClazz));
						} else {
							marshaller = DefaultMarshallers.getByType(type);
						}
					} else {
						marshaller = DefaultMarshallers.getByType(type);
					}
				} else {
					marshaller = DefaultMarshallers.getByType(type);
				}
			} else {
				marshaller = DefaultMarshallers.getByType(type);
			}
		}
		optional = f.getAnnotation(Optional.class) != null;
		if (marshaller == null && type != Boolean.TYPE) {
			String annot = "";
			if (ma != null) {
				annot = "@MarshalledAs(\"" + ma.value().replace("\"", "\\\"") + "\") ";
			}
			if (optional) {
				annot = annot + "@Optional ";
			}
			throw new BadMessageException("Cannot find an appropriate marshaller for field " + annot + type + " " + f.getDeclaringClass().getName() + "." + f.getName());
		}
	}
	
	public T get(Object owner) {
		try {
			return (T)getter.invoke(owner);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	public void set(Object owner, T value) {
		try {
			setter.invoke(owner, value);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	
	public void marshal(Object owner, PacketByteBuf out) {
		T value = get(owner);
		if (value == null) {
			if (isOptional()) return;
			throw new BadMessageException("Wire fields cannot be null (in " + type + " " + f.getDeclaringClass().getName() + "." + f.getName() + ") - did you want to make it @Optional?");
		}
		marshaller.marshal(out, value);
	}
	public void unmarshal(Object owner, PacketByteBuf in) {
		set(owner, marshaller.unmarshal(in));
	}
	
	
	public boolean isOptional() {
		return optional;
	}
	
	
	public Class<? extends T> getType() {
		return type;
	}
}
