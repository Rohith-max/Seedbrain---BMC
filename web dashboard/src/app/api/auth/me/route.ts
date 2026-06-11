import { NextRequest, NextResponse } from 'next/server';
import { verifyToken } from '@/lib/auth/jwt';
import { dataStore } from '@/lib/db/store';

export async function GET(req: NextRequest) {
  try {
    const token = req.cookies.get('nidhi-token')?.value;

    if (!token) {
      return NextResponse.json({ success: false, message: 'Unauthorized' }, { status: 401 });
    }

    const payload = await verifyToken(token);

    if (!payload || !payload.userId) {
      return NextResponse.json({ success: false, message: 'Invalid token' }, { status: 401 });
    }

    // In a real app, query DB. Here we use the demo store
    const user = dataStore.getUser();
    
    // Safety check just for our demo mock
    if (user.id !== payload.userId) {
       return NextResponse.json({ success: false, message: 'User not found' }, { status: 404 });
    }

    return NextResponse.json({
      success: true,
      data: { user }
    });
  } catch (error) {
    return NextResponse.json({ success: false, message: 'Internal server error' }, { status: 500 });
  }
}
