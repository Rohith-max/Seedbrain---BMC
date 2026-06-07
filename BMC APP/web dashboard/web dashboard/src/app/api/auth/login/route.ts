import { NextRequest, NextResponse } from 'next/server';
import { dataStore } from '@/lib/db/store';
import { signToken } from '@/lib/auth/jwt';

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const { email, password } = body;

    if (!email || !password) {
      return NextResponse.json({ success: false, message: 'Email and password are required' }, { status: 400 });
    }

    // Using our demo store to fetch the user
    // In a real app, this would query the DB and verify the hashed password
    const user = dataStore.getUser();
    
    // Demo mock check - if it's the demo user email or any generic test
    if (email === user.email || email === 'demo@nidhi.ai') {
      
      const token = await signToken({
        userId: user.id,
        role: user.role,
        familyId: user.familyId,
      });

      // Log the login activity
      dataStore.addActivityLog({
        id: `log-${Date.now()}`,
        userId: user.id,
        action: 'Logged in',
        entityType: 'auth',
        entityId: user.id,
        createdAt: new Date().toISOString()
      });

      const response = NextResponse.json({
        success: true,
        data: {
          user,
          token
        }
      });

      // Set HTTP-only cookie
      response.cookies.set({
        name: 'nidhi-token',
        value: token,
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'lax',
        maxAge: 60 * 60 * 24 * 7, // 1 week
        path: '/',
      });

      return response;
    }

    return NextResponse.json({ success: false, message: 'Invalid credentials' }, { status: 401 });
  } catch (error) {
    console.error('Login Error:', error);
    return NextResponse.json({ success: false, message: 'Internal server error' }, { status: 500 });
  }
}
