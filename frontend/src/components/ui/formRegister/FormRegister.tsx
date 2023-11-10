import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useRouter } from 'next/router';
import { signIn, useSession } from 'next-auth/react';
import { yupResolver } from '@hookform/resolvers/yup';
import { Button, Typography } from '@mui/material';
import { ErrorMessage } from '@hookform/error-message';
import { CustomTextField } from '@/components/ui/customInput/CustomTextField';
import { schemaRegister } from '@/rules';
import Image from 'next/image';
import GoogleButton from '../googlebutton/GoogleButton';
import Link from 'next/link';

interface FormData {
  email: string;
  username: string;
  password: string;
  repeatPassword: string;
}

const FormRegister = () => {
  const { data: session } = useSession();

  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(schemaRegister),
    reValidateMode: 'onChange',
  });

  const router = useRouter();

  useEffect(() => {
    // Redirect to '/' if the user is signed in and on a different page
    if (session && session.user && router.pathname == '/login') {
      router.push('/');
    }
  }, [session, router]);

  const onSubmit = async (data: FormData) => {
    const { email, username, password } = data;
    // Handle login submission here
    const responseNextAuth = await signIn('credentials', {
      email,
      username,
      password,
      redirect: false,
    });
    console.log(responseNextAuth, data);
  };

  return (
    <div className='container'>
      <div className='container-form'>
        <form onSubmit={handleSubmit(onSubmit)} className='form'>
          <div className='form-top'>
            <h2 className='h2'>Welcome !</h2>
            <h1 className='h1'>
              Log In
            </h1>
            <label htmlFor='email' className='input-label'>
              Email
            </label>
            <input
              {...register('email')}
              type='text'
              placeholder='johndoe@example.com'
              name='email'
              className='input-form'
            />
            <Typography variant="caption" color="red">
              <ErrorMessage errors={errors} name="email" />
            </Typography>

            <label htmlFor='username' className='input-label'>
              Username
            </label>
            <input
              {...register('username')}
              type='text'
              placeholder='johndoe123'
              name='username'
              className='input-form'
            />
            <Typography variant="caption" color="red">
              <ErrorMessage errors={errors} name="username" />
            </Typography>

            <label htmlFor='password' className='input-label'>
              Password
            </label>
            <input
              {...register('password')}
              type='password'
              placeholder='Enter your password'
              name='password'
              className='input-form'
            />
            <Typography variant="caption" color="red">
              <ErrorMessage errors={errors} name="password" />
            </Typography>

            <label htmlFor='repeatPassword' className='input-label'>
            Repeat Password
            </label>
            <input
              {...register('repeatPassword')}
              type='repeatPassword'
              placeholder='Repeat your password'
              name='repeatPassword'
              className='input-form'
            />
            <Typography variant="caption" color="red">
              <ErrorMessage errors={errors} name="repeatPassword" />
            </Typography>

          </div>

          <div className='form-bottom'>
            {/* <div className='flex items-center justify-between pb-6 w-full gap-2'>
                            <label className='flex items-center gap-2'>
                                <input
                                    type='checkbox'
                                    {...register('rememberMe')}
                                    className='accent-primary-color text-primary-color ml-1 hover:text-primary-color focus:ring focus:ring-indigo-200 focus:ring-opacity-50'
                                />
                                Remember Me
                            </label>
                            <a
                                href='/forgot-password'
                                className='text-primary-color hover:underline'
                            >
                                Forgot Password?
                            </a>
                        </div> */}

            <button
              type='submit'
              className='button-form'
            >
              Login
            </button>

            <h3 >or continue with</h3>
            <GoogleButton />
            <div style={{ marginTop: "0.2rem" }}>
              <p className=''>
                Have an account ?{'  '}
                <Link
                  href='/login'
                  className='form_link'
                >
                  Login
                </Link>
              </p>
            </div>
          </div>

        </form>
      </div>
      <div className='container-logo'>
        <Image src="/logo.png" alt={''} width={400} height={400} />
      </div>
    </div>
  );
};

export default FormRegister;

